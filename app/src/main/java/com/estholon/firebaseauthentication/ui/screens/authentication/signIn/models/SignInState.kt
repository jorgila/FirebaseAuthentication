package com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val isPasswordValid: Boolean = false,
    val passwordError: String? = null,
){
    val shouldShowEmailError: Boolean get() = !isLoading && !isSuccess && !error.isNullOrEmpty()
    val shouldNavigateToHome: Boolean get() = !isLoading && isSuccess
}