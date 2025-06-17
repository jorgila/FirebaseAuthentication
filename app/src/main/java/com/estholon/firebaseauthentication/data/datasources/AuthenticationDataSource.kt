package com.estholon.firebaseauthentication.data.datasources

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.google.firebase.auth.FirebaseUser

interface AuthenticationDataSource {

    suspend fun signUpEmail( email: String, password: String) : Result<UserDto?>

}