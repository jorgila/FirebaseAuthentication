package com.estholon.firebaseauthentication.domain.usecases.authentication.twitter

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkTwitterUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkTwitter(activity)
    }

}