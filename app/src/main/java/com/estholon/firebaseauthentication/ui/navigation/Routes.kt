package com.estholon.firebaseauthentication.ui.navigation

sealed class Routes (val route: String){

    object SplashScreen: Routes("splash")
    object SignInScreen: Routes("signin")
    object SignUpScreen: Routes("signup")
    object RecoverScreen: Routes("recover")
    object HomeScreen: Routes("home")
}