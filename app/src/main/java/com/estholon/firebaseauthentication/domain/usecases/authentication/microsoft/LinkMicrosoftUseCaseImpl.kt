package com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkMicrosoftUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkMicrosoftUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkMicrosoft(activity)
    }

}