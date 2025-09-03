package com.estholon.firebaseauthentication.data.datasources.authentication.common

interface CommonAuthenticationDataSource {

    // CHECK STATE
    fun isUserLogged() : Result<Boolean>

    // SIGN OUT
    suspend fun signOut()

}