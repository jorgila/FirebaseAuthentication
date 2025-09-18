package com.estholon.firebaseauthentication.domain.usecases.authentication.github

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkGitHubUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}