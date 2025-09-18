package com.estholon.firebaseauthentication.domain.usecases.authentication.github

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkGitHubUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkGitHubUseCase {

    override suspend operator fun invoke(activity: Activity): Result<UserModel?> {
        return authenticationRepository.linkGitHub(activity)
    }

}