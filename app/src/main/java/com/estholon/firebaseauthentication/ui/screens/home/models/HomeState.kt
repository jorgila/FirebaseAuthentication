package com.estholon.firebaseauthentication.ui.screens.home.models

data class HomeState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val error: String? = null,
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val isPasswordValid: Boolean = false,
    val passwordError: String? = null,
    val logout: Boolean = false
){

}