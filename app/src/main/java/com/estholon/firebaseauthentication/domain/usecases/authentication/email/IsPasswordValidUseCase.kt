package com.estholon.firebaseauthentication.domain.usecases.authentication.email

interface IsPasswordValidUseCase {
    operator fun invoke(password: String) : Result<Unit>
}