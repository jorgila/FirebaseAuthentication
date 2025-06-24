package com.estholon.firebaseauthentication.ui.screens.authentication

data class SignInUiState(
    var isLoading: Boolean = false,
    var isEmailValid: Boolean = true,
    var isPasswordValid: Boolean = true
)