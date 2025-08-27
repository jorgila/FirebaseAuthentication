package com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartEnrollOTPUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) {

    suspend operator fun invoke(phoneNumber: String) : Flow<Result<String>> = flow {
        val session = authenticationRepository.getMultifactorSession()
        val result = authenticationRepository.enrollMfaSendSms(session, phoneNumber)
        emit(result)
    }

}