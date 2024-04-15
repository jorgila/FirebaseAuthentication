package com.estholon.firebaseauthentication.ui.screens.auth

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.AnalyticsManager
import com.estholon.firebaseauthentication.data.AuthRes
import com.estholon.firebaseauthentication.data.AuthService
import com.estholon.firebaseauthentication.data.model.AnalyticModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val analytics: AnalyticsManager
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    // Anonymously Sign In

    fun signUpAnonymously(navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {
            isLoading = true

            when(withContext(Dispatchers.IO){
                authService.signInAnonymously()
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Anonymously", "Successful Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)


                }
                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Anonymously", "Failed Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false
        }
    }

    fun signUpEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {

        viewModelScope.launch {
            isLoading = true

            when (withContext(Dispatchers.IO) {
                authService.signUpWithEmail(email, password)
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign Up",
                        analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)


                }

                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false
        }

    }

    fun onGoogleSignUpSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherSignIn(gsc)
    }

    fun signUpWithGoogle(idToken: String?, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            isLoading = true
            when(withContext(Dispatchers.IO){
                authService.signInWithGoogle(idToken)
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }
            isLoading = false

        }
    }

    fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    )
    {

        viewModelScope.launch {

            isLoading = true

            when(withContext(Dispatchers.IO){
                when (oath) {
                    OathLogin.GitHub -> authService.signInWithGitHub(activity)
                    OathLogin.Microsoft -> authService.signInWithMicrosoft(activity)
                    OathLogin.Twitter -> authService.signInWithTwitter(activity)
                    OathLogin.Yahoo -> authService.signInWithYahoo(activity)
                }
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("$oath", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("$oath", "Failed Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false

        }

    }

    fun signUpWithFacebook(accessToken: AccessToken, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            isLoading = true

            when(withContext(Dispatchers.IO){
                authService.signInWithFacebook(accessToken)
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Facebook", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Facebook", "Failed Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false

        }
    }

}
