package com.estholon.firebaseauthentication.domain.usecases.authentication

import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import javax.inject.Inject

class SignInFacebookUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
)  {
}