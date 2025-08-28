package com.estholon.firebaseauthentication.ui.screens.authentication.otp

import androidx.lifecycle.ViewModel
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignUpEmailUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.signUp.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StartEnrollViewModel @Inject constructor(
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val signUpEmailUseCase: SignUpEmailUseCase
) : ViewModel() {

    // UI STATE
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> get() = _uiState.asStateFlow()

    var signUpAnonymouslyJob: Job? = null

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

    fun Job.cancelIfActive() {
        if (this.isActive) {
            this.cancel()
        }
    }

}