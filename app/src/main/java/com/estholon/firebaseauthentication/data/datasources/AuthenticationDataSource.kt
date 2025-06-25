package com.estholon.firebaseauthentication.data.datasources

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.facebook.AccessToken
import com.google.firebase.auth.PhoneAuthProvider

interface AuthenticationDataSource {

    fun isUserLogged() : Boolean
    suspend fun signUpEmail( email: String, password: String) : Result<UserDto?>
    suspend fun signInEmail( email: String, password: String) : Result<UserDto?>
    suspend fun signInAnonymously() : Result<UserDto?>
    suspend fun signInPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ) : Result<UserDto?>

    suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?>
    suspend fun signInGoogle(activity: Activity) : Result<UserDto?>
    suspend fun handleCredentialResponse(result: GetCredentialResponse):Result<UserDto?>
    suspend fun clearCredentialState()

    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserDto?>
    suspend fun signInGitHub(activity: Activity) : Result<UserDto?>
    suspend fun signInMicrosoft(activity: Activity) : Result<UserDto?>
    suspend fun signInTwitter(activity: Activity) : Result<UserDto?>
    suspend fun signInYahoo(activity: Activity) : Result<UserDto?>
    suspend fun signOut()


    suspend fun resetPassword( email : String ) : Result<Unit>

}