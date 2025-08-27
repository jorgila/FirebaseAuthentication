package com.estholon.firebaseauthentication.ui.screens.authentication.recover

data class RecoverUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val error: String? = null,
)