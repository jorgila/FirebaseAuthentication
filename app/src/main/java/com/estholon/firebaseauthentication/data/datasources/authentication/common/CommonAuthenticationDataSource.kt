package com.estholon.firebaseauthentication.data.datasources.authentication.common

interface CommonAuthenticationDataSource {

    // CHECK STATE
    fun isUserLogged() : Boolean

    // SIGN OUT
    suspend fun signOut()

}