package com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment.models

sealed interface StartEnrollEvent {

    data object ScreenOpened : StartEnrollEvent

    data class SendOTP(val phoneNumber: String) : StartEnrollEvent

}