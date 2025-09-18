package com.estholon.firebaseauthentication.domain.usecases.authentication.email

import javax.inject.Inject

class IsPasswordValidUseCaseImpl @Inject constructor(

) : IsPasswordValidUseCase {

    override operator fun invoke(password: String) : Result<Unit> {
        return when {
            password.isBlank() -> Result.failure(Exception("La contraseña es requerida"))
            password.length < 6 -> Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
//            !password.any { it.isDigit() } -> Result.failure(Exception("La contraseña debe contener al menos un número"))
//            !password.any { it.isUpperCase() } -> Result.failure(Exception("La contraseña debe contener al menos una letra mayúscula"))
//            !password.any { it.isLowerCase() } -> Result.failure(Exception("La contraseña debe contener al menos una letra minúscula"))
            else -> Result.success(Unit)
        }
    }

}