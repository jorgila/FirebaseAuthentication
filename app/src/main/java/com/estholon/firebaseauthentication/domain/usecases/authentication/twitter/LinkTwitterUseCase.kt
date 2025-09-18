package com.estholon.firebaseauthentication.domain.usecases.authentication.twitter

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkTwitterUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}