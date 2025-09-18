package com.estholon.firebaseauthentication.domain.usecases.authentication.google

interface ClearCredentialStateUseCase {
    suspend operator fun invoke()
}