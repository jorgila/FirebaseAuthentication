package com.estholon.firebaseauthentication.domain.usecases.authentication.common

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(){
        authenticationRepository.signOut()
    }

}