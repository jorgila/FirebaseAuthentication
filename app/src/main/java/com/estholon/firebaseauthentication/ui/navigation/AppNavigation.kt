package com.estholon.firebaseauthentication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.estholon.firebaseauthentication.ui.navigation.Routes.*
import com.estholon.firebaseauthentication.ui.screens.authentication.RecoverScreen
import com.estholon.firebaseauthentication.ui.screens.authentication.RecoverViewModel
import com.estholon.firebaseauthentication.ui.screens.authentication.SignInScreen
import com.estholon.firebaseauthentication.ui.screens.authentication.SignInViewModel
import com.estholon.firebaseauthentication.ui.screens.authentication.SignUpScreen
import com.estholon.firebaseauthentication.ui.screens.authentication.SignUpViewModel
import com.estholon.firebaseauthentication.ui.screens.home.HomeScreen
import com.estholon.firebaseauthentication.ui.screens.home.HomeViewModel
import com.estholon.firebaseauthentication.ui.screens.splash.SplashScreen
import com.estholon.firebaseauthentication.ui.screens.splash.SplashViewModel

@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashScreen.route
    ){
        composable(SplashScreen.route){
            val splashViewModel : SplashViewModel = hiltViewModel()
            SplashScreen(
                splashViewModel = splashViewModel,
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(route=HomeScreen.route)
                },
                navigateToSignIn = {
                    navController.popBackStack()
                    navController.navigate(route=SignInScreen.route)
                }
            )
        }
        composable(SignInScreen.route){
            val signInViewModel : SignInViewModel = hiltViewModel()
            SignInScreen(
                signInViewModel = signInViewModel,
                navigateToSignUp = { navController.navigate(route = SignUpScreen.route) },
                navigateToRecover = { navController.navigate(route=RecoverScreen.route) },
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(route = HomeScreen.route)
                }
            )
        }
        composable(SignUpScreen.route){
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                signUpViewModel = signUpViewModel,
                navigateToSignIn = {
                    navController.popBackStack()
                    navController.navigate(route = SignInScreen.route)
                },
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(route = HomeScreen.route)
                },
            )
        }
        composable(RecoverScreen.route){
            val recoverViewModel: RecoverViewModel = hiltViewModel()
            RecoverScreen(
                recoverViewModel = recoverViewModel,
                navigateToSignIn = {
                    navController.popBackStack()
                    navController.navigate(Routes.SignInScreen.route)
                }
            )
        }
        composable(HomeScreen.route){
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                homeViewModel = homeViewModel,
                navigateToSignIn = {
                    navController.popBackStack()
                    navController.navigate(SignInScreen.route)
                }
            )
        }
    }

}