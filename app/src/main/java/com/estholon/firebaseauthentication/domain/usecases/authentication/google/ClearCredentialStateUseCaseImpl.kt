package com.estholon.firebaseauthentication.domain.usecases.authentication.google

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearCredentialStateUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ClearCredentialStateUseCase {

    override suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            authenticationRepository.clearCredentialState()
        }
    }

}