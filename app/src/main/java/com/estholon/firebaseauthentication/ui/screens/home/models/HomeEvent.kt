package com.estholon.firebaseauthentication.ui.screens.home.models

import android.app.Activity
import com.facebook.AccessToken


sealed interface HomeEvent {

    data object Logout : HomeEvent
    data class CheckIfEmailIsValid(val email: String) : HomeEvent
    data class CheckIfPasswordIsValid(val password: String) : HomeEvent
    data class LinkEmail(val email: String, val password: String) : HomeEvent
    data class LinkGoogle(val activity: Activity) : HomeEvent
    data class LinkFacebook(val accessToken: AccessToken) : HomeEvent
    data class LinkGitHub(val activity: Activity) : HomeEvent
    data class LinkMicrosoft(val activity: Activity) : HomeEvent
    data class LinkTwitter(val activity: Activity) : HomeEvent
    data class LinkYahoo(val activity: Activity) : HomeEvent
}