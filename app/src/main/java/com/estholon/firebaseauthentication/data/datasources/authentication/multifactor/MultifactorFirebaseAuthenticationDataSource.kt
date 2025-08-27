package com.estholon.firebaseauthentication.data.datasources.authentication.multifactor

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Handle code sent
                continuation.resume(verificationId)
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
    }

}