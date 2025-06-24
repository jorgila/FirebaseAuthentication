package com.estholon.firebaseauthentication.domain.usecases.authentication

import android.util.Patterns
import javax.inject.Inject

class IsEmailValidUseCase @Inject constructor(

) {

    operator fun invoke( email : String ) : Result<Unit> {
        return when {
            email.isEmpty() -> Result.failure(Exception("El email esta vacío"))
            !Patterns.EMAIL_ADDRESS.matcher( email ).matches() -> Result.failure(Exception("El formato del email no es válido"))
            else -> Result.success(Unit)
        }
    }
}