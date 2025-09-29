package com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models

data class VerificationEmailState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEmailVerified: Boolean = false,
)