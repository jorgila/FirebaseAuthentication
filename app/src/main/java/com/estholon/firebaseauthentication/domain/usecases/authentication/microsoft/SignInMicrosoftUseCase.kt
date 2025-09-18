package com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft

import android.app.Activity

interface SignInMicrosoftUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}