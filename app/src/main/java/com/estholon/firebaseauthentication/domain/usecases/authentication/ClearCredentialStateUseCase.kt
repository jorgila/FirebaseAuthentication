package com.estholon.firebaseauthentication.domain.usecases.authentication

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearCredentialStateUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
){

    suspend operator fun invoke() {
        withContext(Dispatchers.IO){
            authenticationRepository.clearCredentialState()
        }
    }

}