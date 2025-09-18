package com.estholon.firebaseauthentication.domain.usecases.authentication.email

import com.estholon.firebaseauthentication.domain.models.UserModel

interface LinkEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Result<UserModel?>
}