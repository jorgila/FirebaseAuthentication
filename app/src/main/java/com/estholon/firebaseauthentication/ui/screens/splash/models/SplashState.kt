package com.estholon.firebaseauthentication.ui.screens.splash.models

data class SplashState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isUserLogged: Boolean = false,
){
    val shouldNavigateToHome: Boolean get() = isSuccess && isUserLogged
    val shouldNavigateToSignIn: Boolean get() = isSuccess && !isUserLogged
    val shouldShowError: Boolean get() = isError && errorMessage != null
}