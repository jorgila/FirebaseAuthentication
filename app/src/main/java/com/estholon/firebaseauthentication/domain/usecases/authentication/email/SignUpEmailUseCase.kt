package com.estholon.firebaseauthentication.domain.usecases.authentication.email

interface SignUpEmailUseCase {
    suspend operator fun invoke(email: String, password: String) : Result<Unit>
}