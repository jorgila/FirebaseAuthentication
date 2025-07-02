package com.estholon.firebaseauthentication.data.datasources

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.facebook.AccessToken
import com.google.firebase.auth.PhoneAuthProvider

interface AuthenticationDataSource {

    fun isUserLogged() : Boolean
    // EMAIL
    suspend fun signUpEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun linkEmail( email: String, password: String ) : Result<UserDto?>

    // ANONYMOUSLY
    suspend fun signInAnonymously() : Result<UserDto?>
    // PHONE
    suspend fun signInPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ) : Result<UserDto?>

    // GOOGLE
    suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?>
    suspend fun signInGoogle(activity: Activity) : Result<UserDto?>
    suspend fun handleCredentialResponse(result: GetCredentialResponse):Result<UserDto?>
    suspend fun clearCredentialState()
    // FACEBOOK
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserDto?>
    // GITHUB
    suspend fun signInGitHub(activity: Activity) : Result<UserDto?>
    // MICROSOFT
    suspend fun signInMicrosoft(activity: Activity) : Result<UserDto?>
    // TWITTER
    suspend fun signInTwitter(activity: Activity) : Result<UserDto?>
    // YAHOO
    suspend fun signInYahoo(activity: Activity) : Result<UserDto?>
    // SIGN OUT
    suspend fun signOut()


    suspend fun resetPassword( email : String ) : Result<Unit>

}