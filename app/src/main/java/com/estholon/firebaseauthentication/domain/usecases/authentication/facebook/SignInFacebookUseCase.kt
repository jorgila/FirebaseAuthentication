package com.estholon.firebaseauthentication.domain.usecases.authentication.facebook

import com.facebook.AccessToken

interface SignInFacebookUseCase {
    suspend operator fun invoke( accessToken: AccessToken) : Result<Unit>
}