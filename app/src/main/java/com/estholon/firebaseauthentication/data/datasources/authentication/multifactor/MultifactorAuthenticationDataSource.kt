package com.estholon.firebaseauthentication.data.datasources.authentication.multifactor

import com.google.firebase.auth.MultiFactorSession

interface MultifactorAuthenticationDataSource {

    suspend fun getMultifactorSession(): MultiFactorSession
    suspend fun enrollMfaSendSms(session: MultiFactorSession, phoneNumber: String): Result<String>

}