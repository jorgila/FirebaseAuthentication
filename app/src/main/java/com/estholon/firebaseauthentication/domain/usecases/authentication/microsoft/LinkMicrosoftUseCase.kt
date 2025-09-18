package com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkMicrosoftUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}