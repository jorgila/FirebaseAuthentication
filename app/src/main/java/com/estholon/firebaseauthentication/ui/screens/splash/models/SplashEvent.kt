package com.estholon.firebaseauthentication.ui.screens.splash.models

interface SplashEvent {
    data object CheckUserLogin : SplashEvent
    data object RetryLogin : SplashEvent
}