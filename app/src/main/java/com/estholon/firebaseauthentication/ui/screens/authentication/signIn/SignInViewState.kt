package com.estholon.firebaseauthentication.ui.screens.authentication.signIn

data class SignInUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val isPasswordValid: Boolean = false,
    val passwordError: String? = null
)