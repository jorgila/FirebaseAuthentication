package com.estholon.firebaseauthentication.data.datasources.authentication.facebook

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.facebook.AccessToken

interface FacebookAuthenticationDataSource {

    // FACEBOOK
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserDto?>
    suspend fun linkFacebook(accessToken: AccessToken) : Result<UserDto?>

}