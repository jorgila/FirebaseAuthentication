package com.estholon.firebaseauthentication.ui.screens.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkYahooUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val isPasswordValidUseCase: IsPasswordValidUseCase,
    private val linkEmailUseCase: LinkEmailUseCase,
    private val linkGoogleUseCase: LinkGoogleUseCase,
    private val linkFacebookUseCase: LinkFacebookUseCase,
    private val linkGitHubUseCase: LinkGitHubUseCase,
    private val linkMicrosoftUseCase: LinkMicrosoftUseCase,
    private val linkYahooUseCase: LinkYahooUseCase,
    private val linkTwitterUseCase: LinkTwitterUseCase
) : ViewModel() {

    // UI STATE
    private val _uiState = MutableStateFlow<HomeViewState>(HomeViewState())
    val uiState : StateFlow<HomeViewState> get() = _uiState.asStateFlow()

    // Logout

    fun logout(navigateToLogin:()-> Unit) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase()
        }
        navigateToLogin()
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    // EMAIL VALIDATOR

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

    // PASSWORD VALIDATOR

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


    // LINK ACCOUNT WITH EMAIL

    fun onLinkEmail(
        email: String,
        password: String,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkEmailUseCase(email, password)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de email"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }

    // LINK ACCOUNT WITH GOOGLE

    fun onLinkGoogle(
        activity: android.app.Activity,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkGoogleUseCase(activity)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de Google"
                        )
                    }
                    withContext(Dispatchers.Main) {
                        communicateError()
                    }
                }
            )
        }
    }

    // LINK ACCOUNT WITH FACEBOOK

    fun onLinkFacebook(
        accessToken: com.facebook.AccessToken,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkFacebookUseCase(accessToken)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de Facebook"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }

    fun onLinkGitHub(
        activity: Activity,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkGitHubUseCase(activity)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de GitHub"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }

    fun onLinkMicrosoft(
        activity: Activity,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkMicrosoftUseCase(activity)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de Microsoft"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }

    fun onLinkTwitter(
        activity: Activity,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkTwitterUseCase(activity)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de Twitter"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }

    fun onLinkYahoo(
        activity: Activity,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = linkYahooUseCase(activity)
            result.fold(
                onSuccess = { userModel ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateSuccess()
                    }
                },
                onFailure = { exception ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al vincular cuenta de Yahoo"
                        )
                    }
                    withContext(Dispatchers.Main){
                        communicateError()
                    }
                }
            )
        }
    }


}