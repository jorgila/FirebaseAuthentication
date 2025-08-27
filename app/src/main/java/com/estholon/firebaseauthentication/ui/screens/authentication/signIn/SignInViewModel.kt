package com.estholon.firebaseauthentication.ui.screens.authentication.signIn

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.ClearCredentialStateUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignInEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleCredentialManagerUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.SignInYahooUseCase
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInEmailUseCase: SignInEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInGoogleCredentialManagerUseCase: SignInGoogleCredentialManagerUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGitHubUseCase : SignInGitHubUseCase,
    private val signInMicrosoftUseCase: SignInMicrosoftUseCase,
    private val signInTwitterUseCase: SignInTwitterUseCase,
    private val signInYahooUseCase: SignInYahooUseCase,
    private val clearCredentialStateUseCase: ClearCredentialStateUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val isPasswordValidUseCase: IsPasswordValidUseCase
): ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState : StateFlow<SignInUiState> = _uiState.asStateFlow()

    // Check to see if the text entered is an email
    fun isEmailValid(email: String) {
        val result = isEmailValidUseCase(email)
        result.fold(
            onSuccess = {
                _uiState.update { uiState ->
                    uiState.copy(
                        isEmailValid = true
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update { uiState ->
                    uiState.copy(
                        isEmailValid = false,
                        emailError = exception.message.toString()
                    )
                }
            }
        )
    }

    fun isPasswordValid(password: String) {
        val result = isPasswordValidUseCase(password)
        result.fold(
            onSuccess = {
                _uiState.update { uiState ->
                    uiState.copy(
                        isPasswordValid = true
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update { uiState ->
                    uiState.copy(
                        isPasswordValid = false,
                        passwordError = exception.message.toString()
                    )
                }
            }
        )
    }

    // Anonymously Sign In

    fun signInAnonymously(
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {

        viewModelScope.launch {

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = withContext(Dispatchers.IO){
                signInAnonymouslyUseCase()
            }

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            error = exception.message.toString()
                        )
                    }
                    delay(1000)
                    communicateError()
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }
        }

    }


    // Email Sign In

    fun signInEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = signInEmailUseCase(email,password)

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            error = exception.message.toString()
                        )
                    }
                    delay(1000)
                    communicateError()
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }
        }
    }

    // GOOGLE

    fun signInGoogleCredentialManager(activity: Activity, navigateToHome: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = signInGoogleCredentialManagerUseCase(activity)

            result.fold(
                onSuccess = { navigateToHome() },
                onFailure = { exception ->
                    Log.d("SignInViewModel", "Credential Manager no disponible: ${exception.message}")
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }
        }
    }

    fun signInGoogle(
        activity: Activity,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {
        viewModelScope.launch {

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = signInGoogleUseCase(activity)

            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            error = exception.message.toString()
                        )
                    }
                    delay(1000)
                    communicateError()
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }

        }
    }

    fun clearCredentialState(){
        viewModelScope.launch {
            clearCredentialStateUseCase()
        }
    }



    fun signInFacebook(
        accessToken: AccessToken,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {
        viewModelScope.launch {

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = signInFacebookUseCase(accessToken)
            result.fold(
                onSuccess = {
                    navigateToHome()
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            error = exception.message.toString()
                        )
                    }
                    delay(1000)
                    communicateError()
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }

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

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

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
                    _uiState.update { uiState ->
                        uiState.copy(
                            error = exception.message.toString()
                        )
                    }
                    delay(1000)
                    communicateError()
                }
            )

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }

        }

    }

}

sealed class OathLogin {
    object GitHub:OathLogin()
    object Microsoft:OathLogin()
    object Twitter:OathLogin()
    object Yahoo: OathLogin()
}