package com.estholon.firebaseauthentication.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit,
) {

    // Delay to navigate to other screen

    LaunchedEffect(key1 = true) {
        delay(1000)

        if(splashViewModel.isUserLogged()){
            navigateToHome()
        } else {
            navigateToSignIn()
        }

    }

    // Splash Layout

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication",
            modifier = Modifier.fillMaxWidth()
        )
    }
}