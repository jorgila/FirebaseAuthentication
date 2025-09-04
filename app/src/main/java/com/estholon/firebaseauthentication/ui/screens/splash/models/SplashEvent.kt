package com.estholon.firebaseauthentication.ui.screens.splash.models

sealed interface SplashEvent {
    data object CheckUserLogin : SplashEvent
    data object RetryLogin : SplashEvent
    data object NavigationCompleted : SplashEvent
}