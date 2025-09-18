package com.estholon.firebaseauthentication.domain.usecases.authentication.email

interface IsEmailValidUseCase {

    operator fun invoke( email : String ) : Result<Unit>

}