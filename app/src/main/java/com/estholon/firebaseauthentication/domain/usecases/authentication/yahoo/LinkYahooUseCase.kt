package com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkYahooUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}