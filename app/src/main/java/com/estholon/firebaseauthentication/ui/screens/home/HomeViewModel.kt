package com.estholon.firebaseauthentication.ui.screens.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.domain.usecases.authentication.common.SignOutUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.LinkEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.LinkFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.LinkGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.LinkGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.LinkMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.LinkTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.LinkYahooUseCase
import com.estholon.firebaseauthentication.ui.screens.home.models.HomeEvent
import com.estholon.firebaseauthentication.ui.screens.home.models.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val isEmailValidUseCase: IsEmailValidUseCase,
    private val isPasswordValidUseCase: IsPasswordValidUseCase,
    private val linkEmailUseCase: LinkEmailUseCase,
    private val linkGoogleUseCase: LinkGoogleUseCase,
    private val linkFacebookUseCase: LinkFacebookUseCase,
    private val linkGitHubUseCase: LinkGitHubUseCase,
    private val linkMicrosoftUseCase: LinkMicrosoftUseCase,
    private val linkYahooUseCase: LinkYahooUseCase,
    private val linkTwitterUseCase: LinkTwitterUseCase
) : ViewModel() {

    // UI STATE
    private val _state = MutableStateFlow(HomeState())
    val state : StateFlow<HomeState> get() = _state.asStateFlow()

    // JOBS
    @Volatile
    var logoutJob: Job? = null
    @Volatile
    var linkEmailJob: Job? = null
    @Volatile
    var linkGoogleJob: Job? = null
    @Volatile
    var linkFacebookJob: Job? = null
    @Volatile
    var linkAnonymouslyJob: Job? = null
    @Volatile
    var linkOthersJob: Job? = null

    // DISPATCHER

    fun dispatch(event: HomeEvent){
        when(event){
            is HomeEvent.CheckIfEmailIsValid -> {
                isEmailValid(event.email)
            }
            is HomeEvent.CheckIfPasswordIsValid -> {
                isPasswordValid(event.password)
            }
            is HomeEvent.LinkEmail -> {
                onLinkEmail(event.email, event.password)
            }
            is HomeEvent.LinkFacebook -> {
                onLinkFacebook(event.accessToken)
            }
            is HomeEvent.LinkGitHub -> {
                onLinkGitHub(event.activity)
            }
            is HomeEvent.LinkGoogle -> {
                onLinkGoogle(event.activity)
            }
            is HomeEvent.LinkMicrosoft -> {
                onLinkMicrosoft(event.activity)
            }
            is HomeEvent.LinkTwitter -> {
                onLinkTwitter(event.activity)
            }
            is HomeEvent.LinkYahoo -> {
                onLinkYahoo(event.activity)
            }
            is HomeEvent.Logout -> {
                logout()
            }
        }
    }


    // LOGOUT

    private fun logout() {

        if(isJobActive(logoutJob)){
            return
        }

        logoutJob?.cancel()

        logoutJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            // TODO: Improve with result handler
            viewModelScope.launch(Dispatchers.IO) {
                signOutUseCase()
            }

            _state.value = _state.value.copy(
                isLoading = false,
                logout = true
            )
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
                    isEmailValid = true
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

    // LINK ACCOUNT WITH EMAIL

    private fun onLinkEmail(
        email: String,
        password: String
    ) {
        if(isJobActive(linkEmailJob)){
            return
        }

        linkEmailJob?.cancel()

        linkEmailJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkEmailUseCase(email, password)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH GOOGLE

    private fun onLinkGoogle(
        activity: android.app.Activity
    ) {

        if(isJobActive(linkGoogleJob)){
            return
        }

        linkGoogleJob?.cancel()

        linkGoogleJob = viewModelScope.launch(Dispatchers.Main) {

            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkGoogleUseCase(activity)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH FACEBOOK

    private fun onLinkFacebook(
        accessToken: com.facebook.AccessToken
    ) {

        if(isJobActive(linkFacebookJob)){
            return
        }

        linkFacebookJob?.cancel()

        linkFacebookJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkFacebookUseCase(accessToken)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH GITHUB
    private fun onLinkGitHub(
        activity: Activity
    ) {
        if(isJobActive(linkOthersJob)){
            return
        }

        linkOthersJob?.cancel()

        linkOthersJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkGitHubUseCase(activity)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH MICROSOFT
    private fun onLinkMicrosoft(
        activity: Activity
    ) {
        if(isJobActive(linkOthersJob)){
            return
        }

        linkOthersJob?.cancel()

        linkOthersJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkMicrosoftUseCase(activity)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH TWITTER
    private fun onLinkTwitter(
        activity: Activity
    ) {
        if(isJobActive(linkOthersJob)){
            return
        }

        linkOthersJob?.cancel()

        linkOthersJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkTwitterUseCase(activity)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
                        )
                    }
                )
            }
        }
    }

    // LINK ACCOUNT WITH YAHOO
    private fun onLinkYahoo(
        activity: Activity
    ) {
        if(isJobActive(linkOthersJob)){
            return
        }

        linkOthersJob?.cancel()

        linkOthersJob = viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isLoading = true
            )

            viewModelScope.launch(Dispatchers.IO) {
                val result = linkYahooUseCase(activity)
                result.fold(
                    onSuccess = { userModel ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { e ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = e.message ?: "Error al vincular cuenta de email"
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
        logoutJob?.cancel()
        linkEmailJob?.cancel()
        linkGoogleJob?.cancel()
        linkFacebookJob?.cancel()
        linkAnonymouslyJob?.cancel()
        linkOthersJob?.cancel()
    }

    // APPLICATION LIFECYCLE

    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }

}