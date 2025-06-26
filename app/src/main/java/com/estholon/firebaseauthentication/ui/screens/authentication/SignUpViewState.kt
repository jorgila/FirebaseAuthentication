package com.estholon.firebaseauthentication.ui.screens.authentication

data class SignUpUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val error: String? = null,
)