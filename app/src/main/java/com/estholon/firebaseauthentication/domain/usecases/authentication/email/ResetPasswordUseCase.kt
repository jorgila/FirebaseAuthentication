package com.estholon.firebaseauthentication.domain.usecases.authentication.email

interface ResetPasswordUseCase {
    suspend operator fun invoke( email : String ) : Result<Unit>
}