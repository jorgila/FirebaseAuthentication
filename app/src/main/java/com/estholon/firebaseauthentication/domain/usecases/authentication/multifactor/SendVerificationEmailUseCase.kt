package com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor

interface SendVerificationEmailUseCase {

    suspend operator fun invoke(): Result<Unit>

}