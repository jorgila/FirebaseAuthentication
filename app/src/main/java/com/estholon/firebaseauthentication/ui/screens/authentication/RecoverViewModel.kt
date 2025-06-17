package com.estholon.firebaseauthentication.ui.screens.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.managers.AuthRes
import com.estholon.firebaseauthentication.data.managers.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecoverViewModel @Inject constructor(
    private val authService: AuthService
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    fun resetPassword(email: String, navigateToSignIn: () -> Unit, communicateError: () -> Unit) {
        viewModelScope.launch {
            isLoading = true

            when(val result = withContext(Dispatchers.IO){
                authService.resetPassword(email)
            }) {
                is AuthRes.Success -> {
                    navigateToSignIn()
                }
                is AuthRes.Error -> {
                    communicateError()
                }
            }
            isLoading = false
        }
    }
}