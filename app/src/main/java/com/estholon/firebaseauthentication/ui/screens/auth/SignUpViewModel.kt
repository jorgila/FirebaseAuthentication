package com.estholon.firebaseauthentication.ui.screens.auth

import android.app.Activity
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.AnalyticsManager
import com.estholon.firebaseauthentication.data.AuthRes
import com.estholon.firebaseauthentication.data.AuthService
import com.estholon.firebaseauthentication.data.model.AnalyticModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val analytics: AnalyticsManager
): ViewModel() {

    // Progress Indicator Variable

    var isLoading: Boolean by mutableStateOf(false)

    // Error message
    var message : String by mutableStateOf("")

    // Check to see if the text entered is an email
    fun isEmail(user: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(user).matches()
    }

    fun signUpEmail(
        email: String,
        password: String,
        navigateToHome: () -> Unit,
        communicateError: () -> Unit
    ) {

        viewModelScope.launch {
            isLoading = true

            val signUp = authService.signUpWithEmail(email,password)
            when (withContext(Dispatchers.IO) {
                signUp
            }) {
                is AuthRes.Success -> {
                    navigateToHome()

                    val analyticModel = AnalyticModel(
                        title = "Sign Up",
                        analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)


                }

                is AuthRes.Error -> {
                    communicateError()
                    val analyticModel = AnalyticModel(
                        title = "Sign Up", analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                    )
                    analytics.sendEvent(analyticModel)
                }
            }

            isLoading = false
        }

    }

}
