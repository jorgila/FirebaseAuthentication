package com.estholon.firebaseauthentication.domain.usecases.authentication.twitter

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkTwitterUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkTwitterUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkTwitter(activity)
    }

}