package com.estholon.firebaseauthentication.ui.screens.authentication

data class SignUpUiState(
    val isLoading: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val error: String? = null,
)