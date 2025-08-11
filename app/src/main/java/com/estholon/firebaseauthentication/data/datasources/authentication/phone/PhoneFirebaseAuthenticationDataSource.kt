package com.estholon.firebaseauthentication.data.datasources.authentication.phone

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

class PhoneFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : PhoneAuthenticationDataSource {

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

}