package com.estholon.firebaseauthentication.domain.usecases.authentication.facebook

import com.estholon.firebaseauthentication.domain.models.UserModel
import com.facebook.AccessToken

interface LinkFacebookUseCase {
    suspend operator fun invoke(accessToken: AccessToken): Result<UserModel?>
}