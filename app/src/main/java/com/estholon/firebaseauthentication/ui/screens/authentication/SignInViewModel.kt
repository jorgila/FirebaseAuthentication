package com.estholon.firebaseauthentication.ui.screens.authentication

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.managers.AuthRes
import com.estholon.firebaseauthentication.data.managers.AuthService
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGoogleUseCase
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthService,
    private val signInEmailUseCase: SignInEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    @ApplicationContext private val context: Context
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

    fun signInAnonymously(
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {

        viewModelScope.launch {

            isLoading = true

            val result = withContext(Dispatchers.IO){
                signInAnonymouslyUseCase()
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


    // Email Sign In

    fun signInEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true

            val result = signInEmailUseCase(email,password)

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    message = exception.message.toString()
                    communicateError(message)
                }
            )

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

            val result = signInGoogleUseCase(idToken)

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->

                    Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()

                    communicateError(exception.message.toString())
                }
            )

            isLoading = false

        }
    }

    fun signInWithFacebook(accessToken: AccessToken, navigateToHome: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {

            isLoading = true

            val result = signInFacebookUseCase(accessToken)
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