package com.estholon.firebaseauthentication.data

import android.app.Activity
import android.content.Context
import com.estholon.firebaseauthentication.R
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class AuthRes<out T> {
    data class Success<T>(val data: T): AuthRes<T>()
    data class Error(val errorMessage: String): AuthRes<Nothing>()
}

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {

    // User status

    private fun getCurrentUser() = firebaseAuth.currentUser

    fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    // Logout

    fun logout() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
    }

    // Anonymously Sign In and Sign Up
    suspend fun signInAnonymously() : AuthRes<FirebaseUser?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error("Error al iniciar sesión")
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Email Sign Up

    suspend fun signUpWithEmail(email: String, password: String): AuthRes<FirebaseUser?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error("Error al iniciar sesión: $it")
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Email Recover

    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return suspendCancellableCoroutine {cancellableContinuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    val result = AuthRes.Success(Unit)
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener{
                    val result = AuthRes.Error(it.message ?: "Error al restablecer la contraseña")
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Email Sign In
    suspend fun signInWithEmail(user: String, password: String): AuthRes<FirebaseUser?> {

        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithEmailAndPassword(user,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error("Error al iniciar sesión: $it")
                    cancellableContinuation.resume(result)
                }
        }


    }



    private suspend fun initRegisterWithProvider(activity: Activity, provider: OAuthProvider) : AuthRes<FirebaseUser?>{
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.pendingAuthResult
                ?.addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                ?.addOnFailureListener {
                    val result = AuthRes.Error("Error al iniciar sesión: $it")
                    cancellableContinuation.resume(result)
              }?: completeRegisterWithProvider(activity, provider,cancellableContinuation)
        }
    }


    private fun completeRegisterWithProvider(
        activity: Activity,
        provider: OAuthProvider,
        cancellableContinuation: CancellableContinuation<AuthRes<FirebaseUser?>>
    ) {
        firebaseAuth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener {
                val result = if (it.user != null) {
                    AuthRes.Success(it.user)
                } else {
                    AuthRes.Error("Error al iniciar sesión")
                }
                cancellableContinuation.resume(result)
            }
            .addOnFailureListener {
                val result = AuthRes.Error("Error al iniciar sesión: $it")
                cancellableContinuation.resume(result)
            }
    }



    fun signInWithPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ){

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

    suspend fun verifyCode(verificationCode: String, phoneCode: String) : AuthRes<FirebaseUser?> {
        val credentials = PhoneAuthProvider.getCredential(verificationCode, phoneCode)
        return completeRegisterWithCredential(credentials)
    }

    private suspend fun completeRegisterWithCredential(credential: AuthCredential):AuthRes<FirebaseUser?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error("Error al iniciar sesión: $it")
                    cancellableContinuation.resume(result)
                }
        }
    }

    fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context,gso)
    }

    suspend fun signInWithGoogle(idToken: String?): AuthRes<FirebaseUser?> {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        return completeRegisterWithCredential(credential)
    }

    suspend fun completeRegisterWithPhoneVerification(credentials: PhoneAuthCredential) = completeRegisterWithCredential(credentials)

    suspend fun signInWithFacebook(accessToken: AccessToken): AuthRes<FirebaseUser?> {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        return completeRegisterWithCredential(credential)
    }

    suspend fun signInWithGitHub(activity: Activity): AuthRes<FirebaseUser?> {
        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = listOf("user:email")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    suspend fun signInWithMicrosoft(activity: Activity) : AuthRes<FirebaseUser?> {
        val provider = OAuthProvider.newBuilder("microsoft.com").apply{
            scopes = listOf("mail.read","calendars.read")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    suspend fun signInWithTwitter(activity: Activity): AuthRes<FirebaseUser?> {
        val provider = OAuthProvider.newBuilder("twitter.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    suspend fun signInWithYahoo(activity: Activity): AuthRes<FirebaseUser?> {
        val provider = OAuthProvider.newBuilder("yahoo.com").build()
        return initRegisterWithProvider(activity,provider)
    }

}