package com.estholon.firebaseauthentication.data.datasources.authentication.anonymously

import com.estholon.firebaseauthentication.data.dtos.UserDto

interface AnonymouslyAuthenticationDataSource {
    // ANONYMOUSLY
    suspend fun signInAnonymously() : Result<UserDto?>
}