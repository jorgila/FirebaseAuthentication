package com.estholon.firebaseauthentication.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.LinkEmailUseCase
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
    private val linkEmailUseCase: LinkEmailUseCase
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

    fun onLinkWithGoogle(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    fun onLinkWithFacebook(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    fun onLinkWithGitHub(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    fun onLinkWithMicrosoft(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    fun onLinkWithTwitter(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }

    fun onLinkWithYahoo(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = true
            )
        }
        _uiState.update { uiState ->
            uiState.copy(
                isLoading = false
            )
        }
    }


}