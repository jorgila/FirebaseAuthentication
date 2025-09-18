package com.estholon.firebaseauthentication.domain.usecases.authentication.email

import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkEmailUseCase {

    override suspend operator fun invoke(email: String, password: String): Result<UserModel?> {
        return authenticationRepository.linkEmail(email, password)
    }

}