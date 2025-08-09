package com.estholon.firebaseauthentication.data.datasources

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.data.datasources.authentication.EmailAuthenticationDataSource
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val emailAuthenticationDataSource: EmailAuthenticationDataSource,
    private val userMapper: UserMapper,
    @ApplicationContext private val context: Context
) : AuthenticationDataSource {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }



    // USER STATUS

    private fun getCurrentUser() = firebaseAuth.currentUser

    override fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    // ANONYMOUSLY

    //// SIGN IN & SIGN UP

    override suspend fun signInAnonymously(): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception("Error al iniciar sesión"))
                    cancellableContinuation.resume(result)
                }
        }

    }

    // SIGN IN PHONE

    override suspend fun signInPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {

        // Testing with SMS
        // firebaseAuth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+34622413829","123456")

        val options= PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    override suspend fun verifyCode(verificationCode: String, phoneCode: String) : Result<UserDto?> {
        val credentials = PhoneAuthProvider.getCredential(verificationCode, phoneCode)
        return completeRegisterWithCredential(credentials)
    }

    suspend fun completeRegisterWithPhoneVerification(credentials: PhoneAuthCredential) = completeRegisterWithCredential(credentials)


    // SIGN IN GOOGLE

    override suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?> {
        return try {
            val nonce = generateNonce()

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                handleCredentialResponse(result)
            } catch (e: GetCredentialException) {
                signUpWithCredentialManager(activity, nonce)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en signInWithCredentialManager", e)
            Result.failure(e)
        }
    }

    private suspend fun signUpWithCredentialManager(activity: Activity, nonce: String): Result<UserDto?> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al registrarse: ${e.message}"))
        }
    }


    override suspend fun signInGoogle(activity: Activity): Result<UserDto?> {
        return try {
            val nonce = generateNonce()

            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = context.getString(R.string.default_web_client_id)
                )
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al iniciar sesión con Google: ${e.message}"))
        }
    }

    override suspend fun handleCredentialResponse(result: GetCredentialResponse) : Result<UserDto?> {
        val credential = result.credential

        return when (credential) {
            is PublicKeyCredential -> {
                Result.failure(Exception("Passkeys no implementadas aún"))
            }

            is PasswordCredential -> {
                emailAuthenticationDataSource.signInEmail(credential.id, credential.password)
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val userInfo = """
                            ID: ${googleIdTokenCredential.id}
                            Name: ${googleIdTokenCredential.displayName}
                            Email: ${googleIdTokenCredential.givenName}
                        """.trimIndent()
                        Log.d(TAG, "Información del usuario: $userInfo")

                        signInWithGoogleIdToken(idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(Exception("Token de Google inválido"))
                    }
                } else {
                    Result.failure(Exception("Tipo de credencial no soportada"))
                }
            }

            else -> {
                Result.failure(Exception("Tipo de credencial no reconocida"))
            }
        }
    }

    private suspend fun signInWithGoogleIdToken(idToken: String): Result<UserDto?> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return completeRegisterWithCredential(credential)
    }

    override suspend fun linkGoogle(activity: Activity): Result<UserDto?> {
        return try {
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                return Result.failure(Exception("No hay usuario autenticado"))
            }

            val nonce = generateNonce()

            // Try with allowed accounts
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false) // No auto-seleccionar para permitir elegir cuenta
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                handleCredentialResponseForLinking(result, currentUser)
            } catch (e: GetCredentialException) {
                // Try with all accounts
                linkWithCredentialManagerAllAccounts(activity, currentUser, nonce)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en linkGoogle", e)
            Result.failure(Exception("Error al vincular con Google: ${e.message}"))
        }
    }

    private suspend fun linkWithCredentialManagerAllAccounts(
        activity: Activity,
        currentUser: FirebaseUser,
        nonce: String
    ): Result<UserDto?> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Permitir todas las cuentas
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setNonce(nonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponseForLinking(result, currentUser)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al vincular con Google: ${e.message}"))
        }
    }

    private suspend fun handleCredentialResponseForLinking(
        result: GetCredentialResponse,
        currentUser: FirebaseUser
    ): Result<UserDto?> {
        val credential = result.credential

        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val userInfo = """
                            ID: ${googleIdTokenCredential.id}
                            Name: ${googleIdTokenCredential.displayName}
                            Email: ${googleIdTokenCredential.givenName}
                        """.trimIndent()
                        Log.d(TAG, "Vinculando usuario de Google: $userInfo")

                        linkWithGoogleIdToken(currentUser, idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(Exception("Token de Google inválido"))
                    }
                } else {
                    Result.failure(Exception("Tipo de credencial no soportada para vinculación"))
                }
            }
            else -> {
                Result.failure(Exception("Tipo de credencial no reconocida para vinculación"))
            }
        }
    }

    private suspend fun linkWithGoogleIdToken(currentUser: FirebaseUser, idToken: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            // Create credential with token
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Cuenta de Google vinculada exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Google"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Google", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Additional method to link with token
    suspend fun linkWithGoogleIdTokenDirect(idToken: String): Result<UserDto?> {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("No hay usuario autenticado"))
        }
        return linkWithGoogleIdToken(currentUser, idToken)
    }


    // SIGN IN FACEBOOK

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserDto?> {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        return completeRegisterWithCredential(credential)
    }

    override suspend fun linkFacebook(accessToken: AccessToken): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            // Create Facebook credencial with AccessToken
            val credential = FacebookAuthProvider.getCredential(accessToken.token)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Cuenta de Facebook vinculada exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Facebook"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Facebook", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // SIGN IN GITHUB

    override suspend fun signInGitHub(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = listOf("user:email")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH GITHUB
    override suspend fun linkGitHub(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("github.com").apply {
                scopes = listOf("user:email")
            }.build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "GitHub vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de GitHub"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con GitHub", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // SIGN IN MICROSOFT

    override suspend fun signInMicrosoft(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("microsoft.com").apply{
            scopes = listOf("mail.read","calendars.read")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH MICROSOFT
    override suspend fun linkMicrosoft(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            if (isProviderAlreadyLinked("microsoft.com")) {
                val result = Result.failure<UserDto?>(Exception("Ya tienes una cuenta de Microsoft vinculada"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("microsoft.com").apply {
                scopes = listOf("mail.read", "calendars.read")
            }.build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Microsoft vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Microsoft"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Microsoft", exception)

                    // Manejo específico de errores comunes
                    val errorMessage = when {
                        exception.message?.contains("already in use") == true ->
                            "Esta cuenta de Microsoft ya está vinculada a otro usuario"
                        exception.message?.contains("credential-already-in-use") == true ->
                            "Estas credenciales de Microsoft ya están siendo usadas"
                        exception.message?.contains("provider-already-linked") == true ->
                            "Ya tienes una cuenta de Microsoft vinculada"
                        exception.message?.contains("cancelled") == true ->
                            "Operación cancelada por el usuario"
                        else -> exception.message ?: "Error desconocido al vincular cuenta de Microsoft"
                    }

                    val result = Result.failure<UserDto?>(Exception(errorMessage))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // SIGN IN TWITTER

    override suspend fun signInTwitter(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("twitter.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH TWITTER
    override suspend fun linkTwitter(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            if (isProviderAlreadyLinked("twitter.com")) {
                val result = Result.failure<UserDto?>(Exception("Ya tienes una cuenta de Twitter vinculada"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("twitter.com").build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Twitter vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Twitter"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Twitter", exception)

                    // Manejo específico de errores comunes
                    val errorMessage = when {
                        exception.message?.contains("already in use") == true ->
                            "Esta cuenta de Twitter ya está vinculada a otro usuario"
                        exception.message?.contains("credential-already-in-use") == true ->
                            "Estas credenciales de Twitter ya están siendo usadas"
                        exception.message?.contains("provider-already-linked") == true ->
                            "Ya tienes una cuenta de Twitter vinculada"
                        exception.message?.contains("cancelled") == true ->
                            "Operación cancelada por el usuario"
                        exception.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet e intenta nuevamente"
                        else -> exception.message ?: "Error desconocido al vincular cuenta de Twitter"
                    }

                    val result = Result.failure<UserDto?>(Exception(errorMessage))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // SIGN IN YAHOO

    override suspend fun signInYahoo(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("yahoo.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH YAHOO
    override suspend fun linkYahoo(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            if (isProviderAlreadyLinked("yahoo.com")) {
                val result = Result.failure<UserDto?>(Exception("Ya tienes una cuenta de Yahoo vinculada"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("yahoo.com").build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Yahoo vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Yahoo"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Yahoo", exception)

                    // Manejo específico de errores comunes
                    val errorMessage = when {
                        exception.message?.contains("already in use") == true ->
                            "Esta cuenta de Yahoo ya está vinculada a otro usuario"
                        exception.message?.contains("credential-already-in-use") == true ->
                            "Estas credenciales de Yahoo ya están siendo usadas"
                        exception.message?.contains("provider-already-linked") == true ->
                            "Ya tienes una cuenta de Yahoo vinculada"
                        exception.message?.contains("cancelled") == true ->
                            "Operación cancelada por el usuario"
                        exception.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet e intenta nuevamente"
                        exception.message?.contains("web-context-cancelled") == true ->
                            "Proceso de autenticación cancelado"
                        else -> exception.message ?: "Error desconocido al vincular cuenta de Yahoo"
                    }

                    val result = Result.failure<UserDto?>(Exception(errorMessage))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // SIGN OUT OR LOGOUT

    override suspend fun signOut() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
        clearCredentialState()
    }

    // RESET PASSWORD

    override suspend fun resetPassword( email: String ): Result<Unit> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    val result = Result.success(Unit)
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<Unit>(Exception(it.message ?: "Error al restablecer la contraseña"))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // ADDITIONAL FUNCTIONS

    private suspend fun completeRegisterWithCredential(credential: AuthCredential): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    private suspend fun initRegisterWithProvider(activity: Activity, provider: OAuthProvider) : Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.pendingAuthResult
                ?.addOnSuccessListener {
                    val result = if (it.user != null) {
                       Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                ?.addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }?: completeRegisterWithProvider(activity, provider,cancellableContinuation)
        }
    }

    private fun completeRegisterWithProvider(
        activity: Activity,
        provider: OAuthProvider,
        cancellableContinuation: CancellableContinuation<Result<UserDto?>>
    ) {
        firebaseAuth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener {
                val result = if (it.user != null) {
                    Result.success(it.user!!.toUserDto())
                } else {
                    Result.failure(Exception("Error al iniciar sesión"))
                }
                cancellableContinuation.resume(result)
            }
            .addOnFailureListener {
                val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                cancellableContinuation.resume(result)
            }
    }

    override suspend fun clearCredentialState() {
        try {
            val request = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(request)
            Log.d(TAG, "Estado de credenciales limpiado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar estado de credenciales", e)
        }
    }

    private fun generateNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun isProviderAlreadyLinked(providerId: String): Boolean {
        val currentUser = getCurrentUser()
        return currentUser?.providerData?.any { it.providerId == providerId } ?: false
    }

}