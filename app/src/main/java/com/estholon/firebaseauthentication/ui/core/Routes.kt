package com.estholon.firebaseauthentication.ui.core

sealed class Routes (val route: String){

    object SignIn: Routes("signin")
    object SignUp: Routes("signup")
    object Recover: Routes("recover")

    object Home: Routes("home")
}