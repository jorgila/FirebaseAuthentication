package com.estholon.firebaseauthentication.ui.screens.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.ResetPasswordUseCase
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
class RecoverViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(RecoverUiState())
    val uiState : StateFlow<RecoverUiState> = _uiState.asStateFlow()

    // Check to see if the text entered is an email
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
                        error = exception.message.toString()
                    )
                }
            }
        )
    }

    // Progress Indicator Variable

    fun resetPassword(email: String, navigateToSignIn: () -> Unit) {
        viewModelScope.launch {

            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true
                )
            }

            val result = resetPasswordUseCase(email)
            result.fold(
                onSuccess = {
                    navigateToSignIn()
                },
                onFailure = { exception ->
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context,exception.message.toString(), Toast.LENGTH_LONG).show()
                    }
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