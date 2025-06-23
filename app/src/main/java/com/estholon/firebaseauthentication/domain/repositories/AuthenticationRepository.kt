package com.estholon.firebaseauthentication.domain.repositories

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.PhoneAuthProvider

interface AuthenticationRepository {

    fun isUserLogged() : Boolean
    suspend fun signUpEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun signInAnonymously () : Result<UserModel?>
    suspend fun signInPhone(
        phoneNumber:String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ) : Result<UserModel?>
    suspend fun getGoogleClient() : GoogleSignInClient
    suspend fun signInGoogle(idToken: String?) : Result<UserModel?>
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserModel?>
    suspend fun signInGitHub(activity: Activity) : Result<UserModel?>
    suspend fun signInMicrosoft(activity: Activity) : Result<UserModel?>
    suspend fun signInTwitter(activity: Activity) : Result<UserModel?>
    suspend fun signInYahoo(activity: Activity) : Result<UserModel?>
    suspend fun signOut()
    suspend fun resetPassword( email : String ) : Result<Unit>

}