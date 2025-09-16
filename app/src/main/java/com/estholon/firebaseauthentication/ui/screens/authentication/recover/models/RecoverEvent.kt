package com.estholon.firebaseauthentication.ui.screens.authentication.recover.models

import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInEvent

sealed interface RecoverEvent {

    data class CheckIfEmailIsValid(val email: String) : RecoverEvent

    data class ResetPassword(val email: String) : RecoverEvent

}