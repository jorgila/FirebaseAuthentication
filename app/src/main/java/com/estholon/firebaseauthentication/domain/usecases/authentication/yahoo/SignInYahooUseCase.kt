package com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo

import android.app.Activity

interface SignInYahooUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}