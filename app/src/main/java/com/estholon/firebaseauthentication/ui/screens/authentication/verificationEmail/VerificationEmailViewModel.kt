package com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.common.IsEmailVerifiedUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor.SendVerificationEmailUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models.VerificationEmailEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models.VerificationEmailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationEmailViewModel @Inject constructor(
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
): ViewModel(){

    // STATE
    private val _state = MutableStateFlow(VerificationEmailState())
    val state : StateFlow<VerificationEmailState> get() = _state.asStateFlow()

    // INIT
    init {
        isEmailVerified()
    }

    // DISPATCHER
    fun dispatch(event: VerificationEmailEvent){
        when(event){
            VerificationEmailEvent.ScreenOpened -> {

            }
            VerificationEmailEvent.SendEmailVerification -> {
                sendVerificationEmail()
            }
            VerificationEmailEvent.CheckIfEmailIsVerified -> {
                isEmailVerified()
            }
        }
    }

    // METHODS

    private fun isEmailVerified(){
        viewModelScope.launch {
            isEmailVerifiedUseCase()
                .onSuccess { isVerified ->
                    _state.value = _state.value.copy(
                        isEmailVerified = isVerified
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message.toString()
                    )
                }
        }
    }

    private fun sendVerificationEmail(){

        viewModelScope.launch(Dispatchers.IO) {
            sendVerificationEmailUseCase()
                .onSuccess {
                    _state.value = _state.value.copy(
                        isSuccess = true
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isSuccess = false,
                        error = e.message.toString()
                    )
                }
        }

    }
}