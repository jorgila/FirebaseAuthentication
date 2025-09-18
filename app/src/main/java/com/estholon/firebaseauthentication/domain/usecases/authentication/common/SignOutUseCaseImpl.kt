package com.estholon.firebaseauthentication.domain.usecases.authentication.common

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SignOutUseCase {

    override suspend operator fun invoke(){
        authenticationRepository.signOut()
    }

}