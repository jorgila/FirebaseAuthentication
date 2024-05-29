package com.estholon.firebaseauthentication.ui.screens.auth

import android.app.Activity
import android.util.Patterns
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
import kotlin.math.sign

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService,
    private val analytics: AnalyticsManager
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    // Error message
    var message : String by mutableStateOf("")

    // Check to see if the text entered is an email
    fun isEmail(user: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(user).matches()
    }

    // Anonymously Sign In

    fun signinAnonymously(navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {
            isLoading = true

            when(val result = withContext(Dispatchers.IO){
                authService.signInAnonymously()
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Anonymously", "Successful login"))
                    )
                    analytics.sendEvent(analyticModel)


                }
                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Anonymously", "Failed login: ${result.errorMessage}"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }
            isLoading = false
        }
    }


    // Email Sign In

    fun signInEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true

            val signIn = authService.signInWithEmail(email,password)

            when(val result = withContext(Dispatchers.IO){
                signIn
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
                is AuthRes.Error -> {

                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }

                    communicateError(message)
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Failed Sign In: ${result.errorMessage}"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }
            isLoading = false
        }
    }

    fun onGoogleSignInSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherSignIn(gsc)
    }

    fun signInWithGoogle(idToken: String?, navigateToHome: () -> Unit, communicateError: (String) -> Unit) {
        viewModelScope.launch {

            isLoading = true

            val signIn = authService.signInWithGoogle(idToken)

            when(val result = withContext(Dispatchers.IO){
                signIn
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Successful Sign In"))
                    )
                    analytics.sendEvent(analyticModel)
                }
                is AuthRes.Error -> {

                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }

                    communicateError(message)
                    val analyticModel = AnalyticModel(
                        title = "Sign In", analyticsString = listOf(Pair("Email", "Failed Sign In: ${result.errorMessage}"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }
            isLoading = false

        }
    }

    fun signInWithFacebook(accessToken: AccessToken, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            isLoading = true

            when(val result = withContext(Dispatchers.IO){
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
                        title = "Sign In", analyticsString = listOf(Pair("Facebook", "Failed Sign In: ${result.errorMessage}"))
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

            when(val result = withContext(Dispatchers.IO){
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
                        title = "Sign In", analyticsString = listOf(Pair("$oath", "Failed Sign In: ${result.errorMessage}"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false

        }

    }

}

sealed class OathLogin() {
    object GitHub:OathLogin()
    object Microsoft:OathLogin()
    object Twitter:OathLogin()
    object Yahoo: OathLogin()
}