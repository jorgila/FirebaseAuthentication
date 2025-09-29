package com.estholon.firebaseauthentication.data.datasources.authentication.common

interface CommonAuthenticationDataSource {

    // CHECK STATE
    fun isUserLogged() : Result<Boolean>

    suspend fun isEmailVerified() : Result<Boolean>

    // EMAIL VERIFICATION
    suspend fun sendEmailVerification() : Result<Unit>

    // SIGN OUT
    suspend fun signOut()

}