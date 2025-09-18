package com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously

interface SignInAnonymouslyUseCase {

    suspend operator fun invoke() : Result<Unit>

}