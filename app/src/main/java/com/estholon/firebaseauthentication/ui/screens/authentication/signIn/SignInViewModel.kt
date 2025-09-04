package com.estholon.firebaseauthentication.ui.screens.authentication.signIn

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignInEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.ClearCredentialStateUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleCredentialManagerUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.SignInYahooUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInState
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

    // STATE
    
    private val _state = MutableStateFlow(SignInState())
    val state : StateFlow<SignInState> = _state.asStateFlow()

    // DISPATCHER

    fun dispatch(event: SignInEvent) {
        when (event) {
            is SignInEvent.CheckIfEmailIsValid -> {
                isEmailValid(event.email)
            }
            is SignInEvent.CheckIfPasswordIsValid -> {
                isPasswordValid(event.password)
            }
            is SignInEvent.SignInEmail -> {
                signInEmail(event.email, event.password)
            }
            is SignInEvent.SignInFacebook -> {
                signInFacebook(event.accessToken)
            }
            is SignInEvent.SignInAnonymously -> {
                signInAnonymously()
            }
            is SignInEvent.SignInGoogle -> {
                signInGoogle(event.activity)
            }
            is SignInEvent.SignInGoogleCredentialManager -> {
                signInGoogleCredentialManager(event.activity)
            }
            is SignInEvent.OnOathLoginSelected -> {
                onOathLoginSelected(event.oath,event.activity)
            }
        }
    }

    // EMAIL VALIDATOR

    private fun isEmailValid(email: String) {
        _state.value = _state.value.copy(
            isLoading = true
        )
        val result = isEmailValidUseCase(email)
        result.fold(
            onSuccess = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isEmailValid = true,
                    emailError = null
                )
            },
            onFailure = { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isEmailValid = false,
                    emailError = exception.message.toString()
                )
            }
        )
    }

    // PASSWORD VALIDATOR

    private fun isPasswordValid(password: String) {
        _state.value = _state.value.copy(
            isLoading = true
        )
        val result = isPasswordValidUseCase(password)
        result.fold(
            onSuccess = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPasswordValid = true,
                    passwordError = null
                )
            },
            onFailure = { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPasswordValid = false,
                    passwordError = exception.message.toString()
                )
            }
        )
    }

    // EMAIL SIGN IN

    private fun signInEmail(
        email: String,
        password: String
    ) {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = signInEmailUseCase(email,password)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message.toString(),
                    )
                }
            )
        }
    }

    // FACEBOOK SIGN IN

    private fun signInFacebook(
        accessToken: AccessToken,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            val result = signInFacebookUseCase(accessToken)
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message.toString(),
                    )
                }
            )
        }
    }

    // ANONYMOUSLY SIGN IN

    private fun signInAnonymously() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            val result = withContext(Dispatchers.IO){
                signInAnonymouslyUseCase()
            }
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message.toString(),
                    )
                }
            )
        }
    }

    // GOOGLE SIGN IN

    private fun signInGoogleCredentialManager(activity: Activity) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = signInGoogleCredentialManagerUseCase(activity)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = "Credential Manager no disponible",
                    )
                    Log.d("SignInViewModel", "Credential Manager no disponible: ${exception.message}")
                }
            )
        }
    }

    private fun signInGoogle(
        activity: Activity,
    ) {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = signInGoogleUseCase(activity)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message.toString(),
                    )
                }
            )
        }
    }

    private fun clearCredentialState(){
        viewModelScope.launch {
            clearCredentialStateUseCase()
        }
    }

    // OATH SIGN IN

    private fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity
    ) {

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true
            )

            val signIn = when (oath) {
                OathLogin.GitHub -> signInGitHubUseCase(activity)
                OathLogin.Microsoft -> signInMicrosoftUseCase(activity)
                OathLogin.Twitter -> signInTwitterUseCase(activity)
                OathLogin.Yahoo -> signInYahooUseCase(activity)
            }

            signIn.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message.toString(),
                    )
                }
            )

        }

    }

}

sealed class OathLogin {
    object GitHub:OathLogin()
    object Microsoft:OathLogin()
    object Twitter:OathLogin()
    object Yahoo: OathLogin()
}