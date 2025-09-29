package com.estholon.firebaseauthentication.data.datasources.authentication.multifactor

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MultifactorFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): MultifactorAuthenticationDataSource {

    override suspend fun getMultifactorSession(): MultiFactorSession {
        val user = firebaseAuth.currentUser ?: throw Exception("No hay usuario autenticado")
        return user.multiFactor.session.await()
    }

    override suspend fun enrollMfaSendSms(
        session: MultiFactorSession,
        phoneNumber: String
    ): Result<String> = runCatching {
        sendSmsForEnroll(session,phoneNumber)
    }

    suspend fun sendSmsForEnroll(
        session: MultiFactorSession,
        phoneNumber: String,
    ) = suspendCancellableCoroutine<String> { continuation ->
//        firebaseAuth.currentUser?.isEmailVerified?.let { emailVerified ->
//            if (emailVerified) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Handle code sent
                continuation.resume(verificationId)
                GlobalScope.launch {
                    val test = verifySmsForEnroll(verificationId, "123456")
                    Log.d("TAG", "sendSmsForEnroll: $test")
                }
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Handle verification completed
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                continuation.resumeWithException(p0)
            }
        }

        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60, TimeUnit.SECONDS)
            .setMultiFactorSession(session)
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
//            } else {
//                continuation.resumeWithException(Exception("Email not verified"))
//            }
//        } ?: continuation.resumeWithException(Exception("No authenticated user"))
    }


    override suspend fun verifySmsForEnroll(
        verificationId: String,
        verificationCode: String
    ): Result<Unit> = runCatching {
        val phoneProvider = PhoneAuthProvider.getCredential(verificationId, verificationCode)
        val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(phoneProvider)
        firebaseAuth.currentUser?.multiFactor?.enroll(multiFactorAssertion, "Personal number")
            ?.await()
    }

}