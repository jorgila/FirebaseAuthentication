package com.estholon.firebaseauthentication.ui.screens.authentication.recover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.ResetPasswordUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.recover.models.RecoverEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.recover.models.RecoverState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoverViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase
): ViewModel() {

    // STATE
    private val _state = MutableStateFlow(RecoverState())
    val state : StateFlow<RecoverState> = _state.asStateFlow()

    // JOBS

    @Volatile
    var recoverJob: Job? = null

    // DISPATCHER

    fun dispatch (event: RecoverEvent) {
        when (event) {
            is RecoverEvent.CheckIfEmailIsValid -> {
                isEmailValid(event.email)
            }
            is RecoverEvent.ResetPassword -> {
                resetPassword(event.email)
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
                    isLoading = true,
                    isEmailValid = true
                )
            },
            onFailure = { exception ->
                _state.value = _state.value.copy(
                    isLoading = true,
                    isEmailValid = true,
                    emailError = exception.message.toString()
                )
            }
        )
    }

    // Progress Indicator Variable

    private fun resetPassword(
        email: String
    ) {

        if(isJobActive(recoverJob)){
            return
        }

        recoverJob?.cancel()

        recoverJob = viewModelScope.launch(Dispatchers.Main) {

            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = resetPasswordUseCase(email)
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
                        error = exception.message.toString()
                    )
                }
            )
        }
    }

    // JOBS

    private fun isJobActive(job: Job?): Boolean {
        return job?.isActive == true
    }

    private fun cancelAllJobs() {
        recoverJob?.cancel()
    }

    // APPLICATION LIFECYCLE

    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }
}