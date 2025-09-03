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

    var state by mutableStateOf(SplashState())
        private set

    // INIT

    init {
        dispatch(SplashEvent.CheckUserLogin)
    }

    // DISPATCHER

    fun dispatch(event: SplashEvent) {
        when(event) {
            is SplashEvent.CheckUserLogin -> isUserLogged()
            is SplashEvent.RetryLogin -> retryLogin()
        }
    }

    // METHODS

    private fun isUserLogged() {

        viewModelScope.launch(Dispatchers.IO) {


            try {

                val result = isUserLoggedUseCaseImpl()

                result
                    .onStart {
                        state = state.copy(
                            isLoading = true
                        )
                    }
                    .onCompletion {
                        state = state.copy(
                            isLoading = false
                        )
                    }
                    .catch { e ->
                        state = state.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = e.message
                        )
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = {
                                state = state.copy(
                                    isSuccess = true,
                                    isUserLogged = it
                                )
                            },
                            onFailure = { e ->
                                state = state.copy(
                                    isSuccess = false,
                                    isError = true,
                                    isUserLogged = false,
                                    errorMessage = e.message
                                )
                            }
                        )
                    }
            } catch (e: Exception) {
                state = state.copy(
                    isSuccess = false,
                    isError = true,
                    isUserLogged = false,
                    errorMessage = e.message
                )
            }
        }
    }

    private fun retryLogin() {
        state = SplashState()
        dispatch(SplashEvent.CheckUserLogin)
    }
}