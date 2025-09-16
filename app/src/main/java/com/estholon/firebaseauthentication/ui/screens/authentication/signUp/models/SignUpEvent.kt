package com.estholon.firebaseauthentication.ui.screens.authentication.signUp.models

import android.app.Activity
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.OathLogin
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInEvent
import com.facebook.AccessToken

sealed interface SignUpEvent {

    data class CheckIfEmailIsValid(val email: String) : SignUpEvent
    data class CheckIfPasswordIsValid(val password: String) : SignUpEvent
    data class SignUpEmail(val email: String, val password: String) : SignUpEvent

    data class SignUpFacebook(val accessToken: AccessToken) : SignUpEvent

    data object SignUpAnonymously : SignUpEvent
    data class SignUpGoogle(val activity: Activity) : SignUpEvent
    data class SignUpGoogleCredentialManager(val activity: Activity) : SignUpEvent
    data class OnOathLoginSelected(val oath: OathLogin, val activity: Activity) : SignUpEvent


}