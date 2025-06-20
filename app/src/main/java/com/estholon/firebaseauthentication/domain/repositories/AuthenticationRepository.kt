package com.estholon.firebaseauthentication.domain.repositories

import com.estholon.firebaseauthentication.domain.models.UserModel
import com.facebook.AccessToken

interface AuthenticationRepository {

    suspend fun signUpEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserModel?>
    suspend fun signInAnonymously () : Result<UserModel?>
    suspend fun signInGoogle(idToken: String?) : Result<UserModel?>
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserModel?>

}