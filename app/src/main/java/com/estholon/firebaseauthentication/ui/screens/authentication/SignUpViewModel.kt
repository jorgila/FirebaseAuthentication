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
import com.estholon.firebaseauthentication.domain.usecases.authentication.ClearCredentialStateUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGoogleCredentialManagerUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignInYahooUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignUpEmailUseCase
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpEmailUseCase: SignUpEmailUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val clearCredentialStateUseCase: ClearCredentialStateUseCase,
    private val signInYahooUseCase: SignInYahooUseCase,
    private val signInMicrosoftUseCase: SignInMicrosoftUseCase,
    private val signInGitHubUseCase: SignInGitHubUseCase,
    private val signInTwitterUseCase: SignInTwitterUseCase,
): ViewModel() {

    // UI STATE
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState : StateFlow<SignUpUiState> get() = _uiState.asStateFlow()

    // Check to see if the text entered is an email
    fun isEmail(user: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(user).matches()
    }

    // Anonymously Sign In

    fun signUpAnonymously(
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

    // Sign Up with email

    fun signUpEmail(
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


            val result = withContext(Dispatchers.IO){
                signUpEmailUseCase(email,password)
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

    // OTHER METHODS

    fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    )
    {

        viewModelScope.launch {

            val result = when (oath) {
                OathLogin.GitHub -> signInGitHubUseCase(activity)
                OathLogin.Microsoft -> signInMicrosoftUseCase(activity)
                OathLogin.Twitter -> signInTwitterUseCase(activity)
                OathLogin.Yahoo -> signInYahooUseCase(activity)
            }

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            withContext(Dispatchers.IO){
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
                        communicateError()
                    }
                )
            }

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = false
                )
            }

        }

    }

    fun signUpFacebook(
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
