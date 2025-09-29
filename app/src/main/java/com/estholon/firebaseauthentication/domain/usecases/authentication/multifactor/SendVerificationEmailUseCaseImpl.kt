package com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SendVerificationEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : SendVerificationEmailUseCase {

    override suspend fun invoke(): Result<Unit> {
        return authenticationRepository.sendEmailVerification()
    }

}