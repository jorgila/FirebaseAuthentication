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
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignUpEmailUseCase
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
    private val sendEventUseCase: SendEventUseCase,
    private val signUpEmailUseCase: SignUpEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase
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

    fun signUpAnonymously(navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {
            isLoading = true

            val result = withContext(Dispatchers.IO){
                signInAnonymouslyUseCase()
            }

            result.fold(
                onSuccess = {
                    navigateToHome
                },
                onFailure = { exception ->
                    message = exception.message.toString()
                    communicateError()
                }
            )

            isLoading = false
        }
    }

    // Sign Up with email

    fun signUpEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {

        viewModelScope.launch {
            isLoading = true

            val result = withContext(Dispatchers.IO){
                signUpEmailUseCase(email,password)
            }

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    message = exception.message.toString()
                    communicateError()
                }
            )

            isLoading = false
        }

    }

    fun onGoogleSignUpSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherSignIn(gsc)
    }

    fun signUpGoogle(idToken: String?, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            val signUp = authService.signInWithGoogle(idToken)
            isLoading = true
            when(withContext(Dispatchers.IO){
                signUp
            }) {
                is AuthRes.Success -> {
                    navigateToHome()
                }
                is AuthRes.Error -> {
                    signUp.let {
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

            val signUp = when (oath) {
                OathLogin.GitHub -> authService.signInWithGitHub(activity)
                OathLogin.Microsoft -> authService.signInWithMicrosoft(activity)
                OathLogin.Twitter -> authService.signInWithTwitter(activity)
                OathLogin.Yahoo -> authService.signInWithYahoo(activity)
            }

            isLoading = true

            when(withContext(Dispatchers.IO){
                signUp
            }) {
                is AuthRes.Success -> {
                    navigateToHome()
                }
                is AuthRes.Error -> {
                    signUp.let{
                        val string = it.toString().substringAfter("errorMessage=")
                        message = string.substring( 0 , string.length - 1 )
                    }
                    communicateError()
                }
            }

            isLoading = false

        }

    }

    fun signUpWithFacebook(accessToken: AccessToken, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            val signUp = authService.signInWithFacebook(accessToken)
            isLoading = true
            when(withContext(Dispatchers.IO){
                signUp
            }) {
                is AuthRes.Success -> {
                    navigateToHome()
                }
                is AuthRes.Error -> {
                    signUp.let {
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
