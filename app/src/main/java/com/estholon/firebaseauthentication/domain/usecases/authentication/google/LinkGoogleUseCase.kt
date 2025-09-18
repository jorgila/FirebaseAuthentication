package com.estholon.firebaseauthentication.domain.usecases.authentication.google

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkGoogleUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}