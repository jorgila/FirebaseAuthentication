package com.estholon.firebaseauthentication.ui.screens.splash

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes.*
import com.estholon.firebaseauthentication.ui.screens.splash.models.SplashEvent
import com.estholon.firebaseauthentication.ui.screens.splash.models.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    state: State<SplashState> = mutableStateOf(SplashState()),
    onIntent: (SplashEvent) -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignIn: () -> Unit,
) {

    // VARIABLES

    val context = LocalContext.current

    // LAUNCHED EFFECTS

    LaunchedEffect(state.value.shouldNavigateToHome) {
        if (state.value.shouldNavigateToHome) {
            delay(500) // UX delay
            navigateToHome()
        }
    }

    LaunchedEffect(state.value.shouldNavigateToSignIn) {
        if (state.value.shouldNavigateToSignIn) {
            delay(500) // UX delay
            navigateToSignIn()
        }
    }

    LaunchedEffect(state.value.shouldShowError) {
        if (state.value.shouldShowError) {
            Toast.makeText(context, state.value.errorMessage, Toast.LENGTH_LONG).show()
            // AUTO RETRY AFTER 2 SECONDS
            delay(2000)
            onIntent(SplashEvent.RetryLogin)
        }
    }

    // LAYOUT

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