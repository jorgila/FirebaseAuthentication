package com.estholon.firebaseauthentication.ui.navigation

sealed class Routes (val route: String){

    object SplashScreen: Routes("splash")
    object SignInScreen: Routes("signIn")
    object SignUpScreen: Routes("signUp")
    object VerificationEmailScreen: Routes("verificationEmail")
    object StartEnrollScreen: Routes("startEnroll")
    object RecoverScreen: Routes("recover")
    object HomeScreen: Routes("home")
}