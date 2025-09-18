package com.estholon.firebaseauthentication.domain.usecases.authentication.email

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class ResetPasswordUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ResetPasswordUseCase {

    override suspend operator fun invoke( email : String ) : Result<Unit> {
        return authenticationRepository.resetPassword( email )
    }

}