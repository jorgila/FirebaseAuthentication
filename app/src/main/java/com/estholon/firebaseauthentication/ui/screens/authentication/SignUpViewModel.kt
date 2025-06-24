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
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInYahooUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignUpEmailUseCase
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpEmailUseCase: SignUpEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInYahooUseCase: SignInYahooUseCase,
    private val signInMicrosoftUseCase: SignInMicrosoftUseCase,
    private val signInGitHubUseCase: SignInGitHubUseCase,
    private val signInTwitterUseCase: SignInTwitterUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    // Check to see if the text entered is an email
    fun isEmail(user: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(user).matches()
    }

    // Anonymously Sign In

    fun signUpAnonymously(
        navigateToHome: () -> Unit
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
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            )

            isLoading = false

        }
    }

    // Sign Up with email

    fun signUpEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit
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
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            )

            isLoading = false
        }

    }

    fun onGoogleSignUpSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {

        viewModelScope.launch {
            val gsc = signInGoogleUseCase.getGoogleClient()
            googleLauncherSignIn(gsc)
        }

    }

    fun signUpGoogle(
        idToken: String?,
        navigateToHome: () -> Unit
    ) {
        viewModelScope.launch {

            val result = signInGoogleUseCase(idToken)
            isLoading = true
            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            )
            isLoading = false

        }
    }

    fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity,
        navigateToHome: () -> Unit,
    )
    {

        viewModelScope.launch {

            val result = when (oath) {
                OathLogin.GitHub -> signInGitHubUseCase(activity)
                OathLogin.Microsoft -> signInMicrosoftUseCase(activity)
                OathLogin.Twitter -> signInTwitterUseCase(activity)
                OathLogin.Yahoo -> signInYahooUseCase(activity)
            }

            isLoading = true

            withContext(Dispatchers.IO){
                result.fold(
                    onSuccess = {
                        navigateToHome()
                    },
                    onFailure = { exception ->
                        viewModelScope.launch(Dispatchers.Main) {
                            Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }

            isLoading = false

        }

    }

    fun signUpFacebook(
        accessToken: AccessToken,
        navigateToHome: () -> Unit
    ) {
        viewModelScope.launch {

            isLoading = true

            val result = signInFacebookUseCase(accessToken)
            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            )

            isLoading = false

        }
    }
}
