package com.estholon.firebaseauthentication.domain.usecases.authentication

import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import javax.inject.Inject

class LinkFacebookUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(accessToken: AccessToken): Result<UserModel?> {
        return authenticationRepository.linkFacebook(accessToken)
    }

}