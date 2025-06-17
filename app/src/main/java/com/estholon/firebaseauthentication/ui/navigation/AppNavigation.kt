package com.estholon.firebaseauthentication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.estholon.firebaseauthentication.ui.navigation.Routes.*
import com.estholon.firebaseauthentication.ui.screens.authentication.RecoverScreen
import com.estholon.firebaseauthentication.ui.screens.authentication.SignInScreen
import com.estholon.firebaseauthentication.ui.screens.authentication.SignUpScreen
import com.estholon.firebaseauthentication.ui.screens.home.HomeScreen
import com.estholon.firebaseauthentication.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashScreen.route
    ){
        composable(SplashScreen.route){
            SplashScreen(navController = navController)
        }
        composable(SignInScreen.route){
            SignInScreen(navController = navController)
        }
        composable(SignUpScreen.route){
            SignUpScreen(navController = navController)
        }
        composable(RecoverScreen.route){
            RecoverScreen(navController = navController)
        }
        composable(HomeScreen.route){
            HomeScreen(navController = navController)
        }
    }

}