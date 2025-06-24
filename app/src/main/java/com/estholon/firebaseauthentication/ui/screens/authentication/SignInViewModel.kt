package com.estholon.firebaseauthentication.ui.screens.authentication

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInYahooUseCase
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInEmailUseCase: SignInEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGitHubUseCase : SignInGitHubUseCase,
    private val signInMicrosoftUseCase: SignInMicrosoftUseCase,
    private val signInTwitterUseCase: SignInTwitterUseCase,
    private val signInYahooUseCase: SignInYahooUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val isPasswordValidUseCase: IsPasswordValidUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState : StateFlow<SignInUiState> = _uiState.asStateFlow()

    // Check to see if the text entered is an email
    fun isEmailValid(email: String) {
        val result = isEmailValidUseCase(email)
        result.fold(
            onSuccess = {
                _uiState.value.isEmailValid = true
            },
            onFailure = { exception ->
                _uiState.value.error = exception.message.toString()
                _uiState.value.isEmailValid = false
            }
        )
    }

    fun isPasswordValid(password: String) {
        val result = isPasswordValidUseCase(password)
        result.fold(
            onSuccess = {
                _uiState.value.isPasswordValid = true
            },
            onFailure = { exception ->
                _uiState.value.error = exception.message.toString()
                _uiState.value.isPasswordValid = false
            }
        )
    }

    // Anonymously Sign In

    fun signInAnonymously(
        navigateToHome: () -> Unit,
    ) {

        viewModelScope.launch {

            _uiState.value.isLoading = true

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

            _uiState.value.isLoading = false
        }

    }


    // Email Sign In

    fun signInEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.value.isLoading = true

            val result = signInEmailUseCase(email,password)

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

            _uiState.value.isLoading = false
        }
    }

    fun onGoogleSignInSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {

        viewModelScope.launch {
            val gsc = signInGoogleUseCase.getGoogleClient()
            googleLauncherSignIn(gsc)
        }

    }

    fun signInGoogle(
        idToken: String?,
        navigateToHome: () -> Unit
    ) {
        viewModelScope.launch {

            _uiState.value.isLoading = true

            val result = signInGoogleUseCase(idToken)

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

            _uiState.value.isLoading = false

        }
    }

    fun signInFacebook(
        accessToken: AccessToken,
        navigateToHome: () -> Unit,
    ) {
        viewModelScope.launch {

            _uiState.value.isLoading = true

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

            _uiState.value.isLoading = false

        }
    }

    fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity,
        navigateToHome: () -> Unit,
    )
    {

        viewModelScope.launch {

            _uiState.value.isLoading = true

            val signIn = when (oath) {
                OathLogin.GitHub -> signInGitHubUseCase(activity)
                OathLogin.Microsoft -> signInMicrosoftUseCase(activity)
                OathLogin.Twitter -> signInTwitterUseCase(activity)
                OathLogin.Yahoo -> signInYahooUseCase(activity)
            }

            signIn.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            )

            _uiState.value.isLoading = false

        }

    }



}

sealed class OathLogin {
    object GitHub:OathLogin()
    object Microsoft:OathLogin()
    object Twitter:OathLogin()
    object Yahoo: OathLogin()
}