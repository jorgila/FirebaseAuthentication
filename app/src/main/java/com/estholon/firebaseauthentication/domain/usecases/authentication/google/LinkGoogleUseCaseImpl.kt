package com.estholon.firebaseauthentication.domain.usecases.authentication.google

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkGoogleUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkGoogleUseCase {

    override suspend operator fun invoke(activity: Activity): Result<UserModel?> {
        return authenticationRepository.linkGoogle(activity)
    }

}