package com.estholon.firebaseauthentication.ui.screens.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.common.IsUserLoggedUseCaseImpl
import com.estholon.firebaseauthentication.ui.screens.splash.models.SplashEvent
import com.estholon.firebaseauthentication.ui.screens.splash.models.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedUseCaseImpl: IsUserLoggedUseCaseImpl
): ViewModel() {

    // STATE

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()
    
    // INIT

    init {
        dispatch(SplashEvent.CheckUserLogin)
    }

    // DISPATCHER

    fun dispatch(event: SplashEvent) {
        when(event) {
            is SplashEvent.CheckUserLogin -> isUserLogged()
            is SplashEvent.RetryLogin -> retryLogin()
            is SplashEvent.NavigationCompleted -> TODO()
        }
    }

    // METHODS

    private fun isUserLogged() {

        viewModelScope.launch(Dispatchers.IO) {


            try {

                val result = isUserLoggedUseCaseImpl()

                result
                    .onStart {
                        _state.value = _state.value.copy(
                            isLoading = true
                        )
                    }
                    .onCompletion {
                        _state.value = _state.value.copy(
                            isLoading = false
                        )
                    }
                    .catch { e ->
                        _state.value = _state.value.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = e.message
                        )
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = {
                                _state.value = _state.value.copy(
                                    isSuccess = true,
                                    isUserLogged = it
                                )
                            },
                            onFailure = { e ->
                                _state.value = _state.value.copy(
                                    isSuccess = false,
                                    isError = true,
                                    isUserLogged = false,
                                    errorMessage = e.message
                                )
                            }
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSuccess = false,
                    isError = true,
                    isUserLogged = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private fun retryLogin() {
        _state.value = SplashState()
        dispatch(SplashEvent.CheckUserLogin)
    }
}