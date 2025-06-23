package com.estholon.firebaseauthentication.ui.screens.home

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    // Logout

    fun logout(navigateToLogin:()-> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase()
        }
        navigateToLogin()
    }

    // Check to see if the text entered is an email
    fun isEmail(user: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(user).matches()
    }

    fun onLinkWithEmail(
        user: String,
        password: String,
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithGoogle(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithFacebook(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithGitHub(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithMicrosoft(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithTwitter(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }

    fun onLinkWithYahoo(
        communicateSuccess: () -> Unit,
        communicateError: () -> Unit
    ) {
        isLoading = true
        isLoading = false
    }


}