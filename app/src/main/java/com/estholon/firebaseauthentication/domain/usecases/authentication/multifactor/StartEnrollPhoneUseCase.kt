package com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor

import kotlinx.coroutines.flow.Flow

interface StartEnrollPhoneUseCase {
    suspend operator fun invoke(phoneNumber: String): Flow<Result<String>>
}