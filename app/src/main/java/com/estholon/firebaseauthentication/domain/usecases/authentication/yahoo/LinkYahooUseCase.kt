package com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkYahooUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkYahoo(activity)
    }

}