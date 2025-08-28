package com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment.models

data class StartEnrollState (
    val loading: Boolean = false,
    val error: String? = null,
    val onSuccess: Boolean = false
)