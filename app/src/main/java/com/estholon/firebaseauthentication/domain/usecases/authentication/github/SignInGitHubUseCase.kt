package com.estholon.firebaseauthentication.domain.usecases.authentication.github

import android.app.Activity

interface SignInGitHubUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}