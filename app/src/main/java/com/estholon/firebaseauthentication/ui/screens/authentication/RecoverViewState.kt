package com.estholon.firebaseauthentication.ui.screens.authentication

data class RecoverUiState(
    val isLoading: Boolean = false,
    val isEmailValid: Boolean = false,
    val error: String? = null,
)