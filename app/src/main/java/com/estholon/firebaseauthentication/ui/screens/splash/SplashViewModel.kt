package com.estholon.firebaseauthentication.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.estholon.firebaseauthentication.data.managers.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val authService: AuthService): ViewModel() {

    fun isUserLogged():Boolean{
        return authService.isUserLogged()
    }


}