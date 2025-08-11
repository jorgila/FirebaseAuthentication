package com.estholon.firebaseauthentication.data.datasources.authentication.phone

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.google.firebase.auth.PhoneAuthProvider

interface PhoneAuthenticationDataSource {

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

}