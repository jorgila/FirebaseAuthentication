package com.estholon.firebaseauthentication.domain.usecases.authentication.twitter

import android.app.Activity

interface SignInTwitterUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}