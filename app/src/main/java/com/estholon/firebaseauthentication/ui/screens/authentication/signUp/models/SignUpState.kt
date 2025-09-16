package com.estholon.firebaseauthentication.ui.screens.authentication.signUp.models

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val isPasswordValid: Boolean = false,
    val passwordError: String? = null,
    val multifactor: Boolean = false
){
    val shouldShowError: Boolean get() = !isLoading && !isSuccess && !error.isNullOrEmpty()
    val shouldNavigateToHome: Boolean get() = !isLoading && isSuccess && !multifactor
    val shouldNavigateToStartEnroll: Boolean get() = !isLoading && isSuccess && multifactor
}