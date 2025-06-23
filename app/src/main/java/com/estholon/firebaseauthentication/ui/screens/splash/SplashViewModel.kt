package com.estholon.firebaseauthentication.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.estholon.firebaseauthentication.domain.usecases.authentication.IsUserLoggedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedUseCase: IsUserLoggedUseCase
): ViewModel() {

    fun isUserLogged():Boolean{
        return isUserLoggedUseCase()
    }


}