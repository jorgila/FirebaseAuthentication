package com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models

sealed interface VerificationEmailEvent {
    data object ScreenOpened : VerificationEmailEvent
    data object SendEmailVerification : VerificationEmailEvent

    data object CheckIfEmailIsVerified: VerificationEmailEvent
}