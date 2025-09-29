package com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor.StartEnrollPhoneUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment.models.StartEnrollEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment.models.StartEnrollState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartEnrollViewModel @Inject constructor(
    private val startEnrollPhoneUseCase: StartEnrollPhoneUseCase
) : ViewModel() {

    private var startEnrollJob: Job? = null

    var state by mutableStateOf(StartEnrollState())
        private set

    fun dispatch(event: StartEnrollEvent){
        when(event){
            is StartEnrollEvent.ScreenOpened -> TODO()
            is StartEnrollEvent.SendOTP -> sendOTP(phoneNumber = event.phoneNumber)
        }
    }

    private fun sendOTP(phoneNumber: String) {
        startEnrollJob?.cancelIfActive()
        startEnrollJob = viewModelScope.launch(Dispatchers.IO) {
            startEnrollPhoneUseCase(phoneNumber = phoneNumber)
                .onStart {
                    state = state.copy(loading = true)
                }
                .onCompletion {
                    state = state.copy(loading = false)
                }
                .catch { exception ->
                    state = state.copy(
                        error = exception.message,
                        onSuccess = false
                    )
                }
                .collect{ result ->
                    result.fold(
                        onSuccess = {
                            state = state.copy(
                                onSuccess = true
                            )
                        },
                        onFailure = { exception ->
                            state = state.copy(
                                error = exception.message,
                                onSuccess = false
                            )
                        }
                    )
                }
        }

    }

    private fun Job.cancelIfActive() {
        if (this.isActive) {
            this.cancel()
        }
    }

}