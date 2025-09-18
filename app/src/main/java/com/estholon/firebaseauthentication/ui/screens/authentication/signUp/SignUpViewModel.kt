package com.estholon.firebaseauthentication.ui.screens.authentication.signUp

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously.SignInAnonymouslyUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignUpEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.ClearCredentialStateUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleCredentialManagerUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.SignInYahooUseCase
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.OathLogin
import com.estholon.firebaseauthentication.ui.screens.authentication.signUp.models.SignUpEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.signUp.models.SignUpState
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpEmailUseCase: SignUpEmailUseCase,
    private val signInAnonymouslyUseCaseImpl: SignInAnonymouslyUseCaseImpl,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleCredentialManagerUseCase: SignInGoogleCredentialManagerUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val clearCredentialStateUseCase: ClearCredentialStateUseCase,
    private val signInYahooUseCase: SignInYahooUseCase,
    private val signInMicrosoftUseCase: SignInMicrosoftUseCase,
    private val signInGitHubUseCase: SignInGitHubUseCase,
    private val signInTwitterUseCase: SignInTwitterUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val isPasswordValidUseCase: IsPasswordValidUseCase
): ViewModel() {

    // UI STATE
    private val _state = MutableStateFlow(SignUpState())
    val state : StateFlow<SignUpState> get() = _state.asStateFlow()

    // JOBS
    @Volatile
    var signUpEmailJob: Job? = null
    @Volatile
    var signInGoogleCredentialJob: Job? = null
    @Volatile
    var signUpGoogleJob: Job? = null
    @Volatile
    var signUpFacebookJob: Job? = null
    @Volatile
    var signUpAnonymouslyJob: Job? = null
    @Volatile
    var signUpOthersJob: Job? = null

    // DISPATCHER
    fun dispatch(event: SignUpEvent){
        when(event){
            is SignUpEvent.CheckIfEmailIsValid -> {
                isEmailValid(event.email)
            }
            is SignUpEvent.CheckIfPasswordIsValid -> {
                isPasswordValid(event.password)
            }
            is SignUpEvent.SignUpEmail -> {
                signUpEmail(event.email,event.password)
            }
            is SignUpEvent.SignUpFacebook -> {
                signUpFacebook(event.accessToken)
            }
            is SignUpEvent.OnOathLoginSelected -> {
                onOathLoginSelected(event.oath,event.activity)
            }
            is SignUpEvent.SignUpAnonymously -> {
                signUpAnonymously()
            }
            is SignUpEvent.SignUpGoogle -> {
                signInGoogle(event.activity)
            }
            is SignUpEvent.SignUpGoogleCredentialManager -> {
                signInGoogleCredentialManager(event.activity)
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
                    isLoading = false,
                    isEmailValid = true,
                    multifactor = true
                )
            },
            onFailure = { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isEmailValid = false,
                    emailError = exception.message.toString()
                )
            }
        )
    }

    // PASSWORD VALIDATOR

    private fun isPasswordValid(password: String) {
        _state.value = _state.value.copy(
            isLoading = true
        )
        val result = isPasswordValidUseCase(password)
        result.fold(
            onSuccess = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPasswordValid = true
                )
            },
            onFailure = { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isPasswordValid = false,
                    passwordError = exception.message.toString()
                )
            }
        )
    }

    // EMAIL SIGN UP

    private fun signUpEmail(
        email: String,
        password: String
    ) {

        if(isJobActive(signUpEmailJob)){
            return
        }

        signUpEmailJob?.cancel()

        signUpEmailJob = viewModelScope.launch(Dispatchers.Main) {

        _state.value = _state.value.copy(
                isLoading = true
            )
            val result = withContext(Dispatchers.IO){
                signUpEmailUseCase(email,password)
            }
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
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

    // FACEBOOK SIGN UP

    private fun signUpFacebook(
        accessToken: AccessToken
    ) {

        if(isJobActive(signUpFacebookJob)){
            return
        }

        signUpFacebookJob?.cancel()

        signUpFacebookJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )
            try {
                val result = signInFacebookUseCase(accessToken)
                result.fold(
                    onSuccess = {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
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
            } catch (e: Exception){
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = e.message.toString()
                )
            }
        }
    }


    // ANONYMOUSLY SIGN UP

    private fun signUpAnonymously() {

        if(isJobActive(signUpAnonymouslyJob)){
            return
        }

        signUpAnonymouslyJob?.cancel()

        signUpAnonymouslyJob = viewModelScope.launch(Dispatchers.Main) {

            _state.value = _state.value.copy(
                isLoading = true
            )

            try {

                val result =
                    withContext(Dispatchers.IO){
                        ensureActive()
                        signInAnonymouslyUseCaseImpl()
                    }

                ensureActive()

                result.fold(
                    onSuccess = {
                        ensureActive()
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        ensureActive()
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message.toString()
                        )
                    }
                )

            } catch (e: Exception) {
                if (e !is CancellationException){
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = e.message.toString()
                    )
                }
            }
        }
    }


    // GOOGLE SIGN UP

    private fun signInGoogleCredentialManager(activity: Activity) {
        if(isJobActive(signInGoogleCredentialJob)){
            return
        }

        signInGoogleCredentialJob?.cancel()

        signInGoogleCredentialJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = signInGoogleCredentialManagerUseCase(activity)

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
                        error = "Credential Manager no disponible",
                    )
                    Log.d("SignInViewModel", "Credential Manager no disponible: ${exception.message}")
                }
            )
        }
    }

    private fun signInGoogle(
        activity: Activity
    ) {

        if(isJobActive(signUpGoogleJob)){
            return
        }

        signUpGoogleJob?.cancel()

        signUpGoogleJob = viewModelScope.launch(Dispatchers.Main) {

            _state.value = _state.value.copy(
                isLoading = true
            )

            val result = signInGoogleUseCase(activity)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
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

    private fun clearCredentialState(){
        viewModelScope.launch {
            clearCredentialStateUseCase()
        }
    }

    // OTHER METHODS

    private fun onOathLoginSelected(
        oath: OathLogin,
        activity: Activity
    ) {

        if(isJobActive(signUpOthersJob)){
            return
        }

        signUpOthersJob?.cancel()

        signUpOthersJob = viewModelScope.launch(Dispatchers.Main) {

            val result = when (oath) {
                OathLogin.GitHub -> signInGitHubUseCase(activity)
                OathLogin.Microsoft -> signInMicrosoftUseCase(activity)
                OathLogin.Twitter -> signInTwitterUseCase(activity)
                OathLogin.Yahoo -> signInYahooUseCase(activity)
            }

            _state.value = _state.value.copy(
                isLoading = false
            )

            withContext(Dispatchers.IO){
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
    }

    // JOBS

    private fun isJobActive(job: Job?): Boolean {
        return job?.isActive == true
    }

    private fun cancelAllJobs() {
        signUpEmailJob?.cancel()
        signUpGoogleJob?.cancel()
        signUpFacebookJob?.cancel()
        signUpAnonymouslyJob?.cancel()
        signUpOthersJob?.cancel()
    }

    // APPLICATION LIFECYCLE

    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }

}
