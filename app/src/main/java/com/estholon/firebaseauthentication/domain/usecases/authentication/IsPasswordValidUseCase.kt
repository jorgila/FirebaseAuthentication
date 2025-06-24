package com.estholon.firebaseauthentication.domain.usecases.authentication

import javax.inject.Inject

class IsPasswordValidUseCase @Inject constructor(

) {

    operator fun invoke(password: String) : Result<Unit> {
        return when {
            password.isBlank() -> Result.failure(Exception("La contraseña es requerida"))
            password.length < 6 -> Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            else -> Result.success(Unit)
        }
    }

}