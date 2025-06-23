package com.estholon.firebaseauthentication.ui.screens.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecoverViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    fun resetPassword(email: String, navigateToSignIn: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {
            isLoading = true

            withContext(Dispatchers.IO){
                val result = resetPasswordUseCase(email)
                result.fold(
                    onSuccess = {
                        navigateToSignIn()
                    },
                    onFailure = { exception ->
                        communicateError()
                    }
                )
            }

            isLoading = false
        }
    }
}