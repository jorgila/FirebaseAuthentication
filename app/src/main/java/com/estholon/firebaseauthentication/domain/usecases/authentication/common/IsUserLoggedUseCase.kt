package com.estholon.firebaseauthentication.domain.usecases.authentication.common

import kotlinx.coroutines.flow.Flow

interface IsUserLoggedUseCase {
    suspend operator fun invoke(): Flow<Result<Boolean>>
}