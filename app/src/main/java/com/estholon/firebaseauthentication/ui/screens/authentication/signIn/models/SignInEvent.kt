package com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models

import android.app.Activity
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.OathLogin
import com.facebook.AccessToken

interface SignInEvent {

    data class CheckIfEmailIsValid(val email: String) : SignInEvent
    data class CheckIfPasswordIsValid(val password: String) : SignInEvent
    data class SignInEmail(val email: String, val password: String) : SignInEvent

    data class SignInFacebook(val accessToken: AccessToken) : SignInEvent

    data object SignInAnonymously : SignInEvent
    data class SignInGoogle(val activity: Activity) : SignInEvent
    data class SignInGoogleCredentialManager(val activity: Activity) : SignInEvent
    data class OnOathLoginSelected(val oath: OathLogin, val activity: Activity) : SignInEvent

}