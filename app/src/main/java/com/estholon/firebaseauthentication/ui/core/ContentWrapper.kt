package com.estholon.firebaseauthentication.ui.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.estholon.firebaseauthentication.ui.core.Routes.*
import com.estholon.firebaseauthentication.ui.screens.auth.RecoverScreen
import com.estholon.firebaseauthentication.ui.screens.auth.SignInScreen
import com.estholon.firebaseauthentication.ui.screens.auth.SignUpScreen
import com.estholon.firebaseauthentication.ui.screens.home.HomeScreen

@Composable
fun ContentWrapper(navHostController: NavHostController){
    NavHost(
        navController = navHostController,
        startDestination = SignIn.route
    ){
        composable(SignIn.route){
            SignInScreen()
        }
        composable(SignUp.route){
            SignUpScreen()
        }
        composable(Recover.route){
            RecoverScreen()
        }
        composable(Home.route){
            HomeScreen()
        }
    }

}