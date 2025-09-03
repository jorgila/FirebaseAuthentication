package com.estholon.firebaseauthentication.domain.usecases.authentication.common

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IsUserLoggedUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : IsUserLoggedUseCase {

    override suspend operator fun invoke() : Flow<Result<Boolean>> = flow {
        val result = authenticationRepository.isUserLogged()
        emit(result)
    }

}