package com.estholon.firebaseauthentication.domain.usecases.authentication.common

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class IsEmailVerifiedUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): IsEmailVerifiedUseCase {
    override suspend fun invoke(): Result<Boolean> {
        return authenticationRepository.isEmailVerified()
    }
}