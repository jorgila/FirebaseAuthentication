package com.estholon.firebaseauthentication.ui.screens.authentication

data class SignInUiState(
    var isLoading: Boolean = false,
    var isEmailValid: Boolean = false,
    var isPasswordValid: Boolean = false,
    var error: String? = null,
)