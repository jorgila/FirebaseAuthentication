package com.estholon.firebaseauthentication.data.datasources.authentication

import com.estholon.firebaseauthentication.data.dtos.UserDto

interface EmailAuthenticationDataSource {

    suspend fun signUpEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun linkEmail( email: String, password: String ) : Result<UserDto?>

}