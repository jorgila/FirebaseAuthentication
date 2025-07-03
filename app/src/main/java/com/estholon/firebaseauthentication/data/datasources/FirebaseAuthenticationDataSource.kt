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
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    private val userMapper: UserMapper,
    @ApplicationContext private val context: Context
) : AuthenticationDataSource {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    private fun FirebaseUser.toUserDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        phoneNumber = phoneNumber
    )

    // USER STATUS

    private fun getCurrentUser() = firebaseAuth.currentUser

    override fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    // EMAIL

    //// SIGN UP

    override suspend fun signUpEmail(email: String, password: String) : Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
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

    //// SIGN IN

    override suspend fun signInEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithEmailAndPassword(email,password)
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

    //// LINK

    override suspend fun linkEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = getCurrentUser()
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            // Create credential of email and password
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de email"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
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
                signInEmail(credential.id, credential.password)
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
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("No hay usuario autenticado"))
        }

        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = listOf("user:email")
        }.build()

        return initLinkWithProvider(activity, provider, currentUser)
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
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("No hay usuario autenticado"))
        }

        val provider = OAuthProvider.newBuilder("microsoft.com").apply {
            scopes = listOf("mail.read", "calendars.read")
        }.build()

        return initLinkWithProvider(activity, provider, currentUser)
    }

    // SIGN IN TWITTER

    override suspend fun signInTwitter(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("twitter.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH TWITTER
    override suspend fun linkTwitter(activity: Activity): Result<UserDto?> {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("No hay usuario autenticado"))
        }

        val provider = OAuthProvider.newBuilder("twitter.com").build()

        return initLinkWithProvider(activity, provider, currentUser)
    }

    // SIGN IN YAHOO

    override suspend fun signInYahoo(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("yahoo.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH YAHOO
    override suspend fun linkYahoo(activity: Activity): Result<UserDto?> {
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            return Result.failure(Exception("No hay usuario autenticado"))
        }

        val provider = OAuthProvider.newBuilder("yahoo.com").build()

        return initLinkWithProvider(activity, provider, currentUser)
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

    private fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context,gso)
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

    // AUXILIARY METHOD TO LINK WITH PROVIDERS OAUTH
    private suspend fun initLinkWithProvider(
        activity: Activity,
        provider: OAuthProvider,
        currentUser: FirebaseUser
    ): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            // Verify if there is a pending result
            firebaseAuth.pendingAuthResult
                ?.addOnSuccessListener { authResult ->
                    // Try to link if there is a pending result
                    if (authResult.user != null && authResult.credential != null) {
                        linkCredentialToCurrentUser(currentUser, authResult.credential!!, cancellableContinuation)
                    } else {
                        val result = Result.failure<UserDto?>(Exception("Error: resultado de autenticación incompleto"))
                        cancellableContinuation.resume(result)
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.e(TAG, "Error en resultado pendiente", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                } ?: completeLinkWithProvider(activity, provider, currentUser, cancellableContinuation)
        }
    }

    // AUXILIARY METHOD TO COMPLETE LINK WITH PROVIDER
    private fun completeLinkWithProvider(
        activity: Activity,
        provider: OAuthProvider,
        currentUser: FirebaseUser,
        cancellableContinuation: CancellableContinuation<Result<UserDto?>>
    ) {
        firebaseAuth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener { authResult ->
                if (authResult.user != null && authResult.credential != null) {
                    // En lugar de crear un nuevo usuario, vincular la credencial al usuario actual
                    linkCredentialToCurrentUser(currentUser, authResult.credential!!, cancellableContinuation)
                } else {
                    val result = Result.failure<UserDto?>(Exception("Error: credencial no disponible"))
                    cancellableContinuation.resume(result)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al vincular con proveedor OAuth", exception)
                val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                cancellableContinuation.resume(result)
            }
    }

    // AUXILIARY METHOD TO LINK CREDENTIAL TO CURRENT USER
    private fun linkCredentialToCurrentUser(
        currentUser: FirebaseUser,
        credential: AuthCredential,
        cancellableContinuation: CancellableContinuation<Result<UserDto?>>
    ) {
        currentUser.linkWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val result = if (authResult.user != null) {
                    Log.d(TAG, "Proveedor OAuth vinculado exitosamente")
                    Result.success(authResult.user!!.toUserDto())
                } else {
                    Result.failure(Exception("Error al vincular proveedor OAuth"))
                }
                cancellableContinuation.resume(result)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al vincular credencial OAuth", exception)
                val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                cancellableContinuation.resume(result)
            }
    }


}