package com.estholon.firebaseauthentication.domain.repositories

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.auth.User

interface AuthenticationRepository {

    fun isUserLogged() : Result<Boolean>

    // EMAIL
    suspend fun signUpEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun linkEmail(email: String, password: String): Result<UserModel?>

    // ANONYMOUSLY
    suspend fun signInAnonymously () : Result<UserModel?>

    // PHONE
    suspend fun signInPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ) : Result<UserModel?>

    // GOOGLE
    suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserModel?>
    suspend fun signInGoogle(activity: Activity) : Result<UserModel?>
    suspend fun handleCredentialResponse(result: GetCredentialResponse):Result<UserModel?>
    suspend fun clearCredentialState()
    suspend fun linkGoogle(activity: Activity): Result<UserModel?>

    // FACEBOOK
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserModel?>
    suspend fun linkFacebook(accessToken: AccessToken): Result<UserModel?>

    // GITHUB
    suspend fun signInGitHub(activity: Activity) : Result<UserModel?>
    suspend fun linkGitHub( activity: Activity ) : Result<UserModel?>

    // MICROSOFT
    suspend fun signInMicrosoft(activity: Activity) : Result<UserModel?>
    suspend fun linkMicrosoft( activity: Activity ) : Result<UserModel?>

    // TWITTER
    suspend fun signInTwitter(activity: Activity) : Result<UserModel?>
    suspend fun linkTwitter( activity: Activity ) : Result<UserModel?>

    // YAHOO
    suspend fun signInYahoo(activity: Activity) : Result<UserModel?>
    suspend fun linkYahoo( activity: Activity ) : Result<UserModel?>

    // SIGN OUT
    suspend fun signOut()

    // RESET PASSWORD
    suspend fun resetPassword( email : String ) : Result<Unit>

    // MFA
    suspend fun getMultifactorSession(): MultiFactorSession
    suspend fun enrollMfaSendSms(session: MultiFactorSession, phoneNumber: String) : Result<String>

}