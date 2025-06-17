package com.estholon.firebaseauthentication.domain.repositories

import com.estholon.firebaseauthentication.domain.models.UserModel

interface AuthenticationRepository {

    suspend fun signUpEmail( email: String, password: String) : Result<UserModel?>

}