package com.estholon.firebaseauthentication.domain.usecases.authentication.google

import android.app.Activity

interface SignInGoogleCredentialManagerUseCase {
    suspend operator fun invoke(activity: Activity) : Result<Unit>
}