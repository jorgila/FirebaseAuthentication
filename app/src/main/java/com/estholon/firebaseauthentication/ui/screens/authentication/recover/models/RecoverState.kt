package com.estholon.firebaseauthentication.ui.screens.authentication.recover.models

data class RecoverState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
){
    val shouldShowError: Boolean get() = !isLoading && !isSuccess && !error.isNullOrEmpty()
    val shouldNavigateToSignIn: Boolean get() = !isLoading && isSuccess
}