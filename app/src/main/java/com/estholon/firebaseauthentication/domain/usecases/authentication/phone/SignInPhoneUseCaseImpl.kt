package com.estholon.firebaseauthentication.domain.usecases.authentication.phone

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import javax.inject.Inject

class SignInPhoneUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
)  : SignInPhoneUseCase {
}