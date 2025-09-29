package com.estholon.firebaseauthentication.domain.usecases.authentication.common

interface IsEmailVerifiedUseCase {

    suspend operator fun invoke() : Result<Boolean>

}