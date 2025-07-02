package com.estholon.firebaseauthentication.ui.screens.home

data class HomeViewState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val isPasswordValid: Boolean = false,
    val passwordError: String? = null,
    val error: String? = null
)