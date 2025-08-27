package com.estholon.firebaseauthentication.domain.usecases.authentication.email

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke( email : String ) : Result<Unit> {
        return authenticationRepository.resetPassword( email )
    }

}