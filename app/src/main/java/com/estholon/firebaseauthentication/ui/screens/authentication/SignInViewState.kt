package com.estholon.firebaseauthentication.ui.screens.authentication

data class SignInUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val error: String? = null,
)