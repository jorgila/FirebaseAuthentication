package com.estholon.firebaseauthentication.data.datasources

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser

interface AuthenticationDataSource {

    suspend fun signUpEmail( email: String, password: String) : Result<UserDto?>
    suspend fun signInEmail( email: String, password: String) : Result<UserDto?>
    suspend fun signInAnonymously() : Result<UserDto?>
    suspend fun signInGoogle(idToken: String?) : Result<UserDto?>
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserDto?>
    suspend fun signInWithGitHub() : Result<UserDto?>
    suspend fun signInWithMicrosoft() : Result<UserDto?>
    suspend fun signInWithTwitter() : Result<UserDto?>
    suspend fun signInWithYahoo() : Result<UserDto?>

}