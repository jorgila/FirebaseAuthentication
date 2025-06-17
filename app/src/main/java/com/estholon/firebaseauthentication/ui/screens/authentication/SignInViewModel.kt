package com.estholon.firebaseauthentication.ui.screens.authentication

import android.app.Activity
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.managers.AuthRes
import com.estholon.firebaseauthentication.data.managers.AuthService
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService
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
                }
                is AuthRes.Error -> {
                    communicateError()
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
                }
                is AuthRes.Error -> {

                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }

                    communicateError(message)
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
                }
                is AuthRes.Error -> {

                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }

                    communicateError(message)
                }
            }
            isLoading = false

        }
    }

    fun signInWithFacebook(accessToken: AccessToken, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            isLoading = true

            val signIn = authService.signInWithFacebook(accessToken)
            when(val result = withContext(Dispatchers.IO){
                signIn
            }) {
                is AuthRes.Success -> {
                    navigateToHome()
                }
                is AuthRes.Error -> {
                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }
                    communicateError()
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

            val signIn = when (oath) {
                OathLogin.GitHub -> authService.signInWithGitHub(activity)
                OathLogin.Microsoft -> authService.signInWithMicrosoft(activity)
                OathLogin.Twitter -> authService.signInWithTwitter(activity)
                OathLogin.Yahoo -> authService.signInWithYahoo(activity)
            }


            when(val result = withContext(Dispatchers.IO){
                signIn
            }) {
                is AuthRes.Success -> {
                    navigateToHome()
                }
                is AuthRes.Error -> {
                    signIn.let {
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }
                    communicateError()
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