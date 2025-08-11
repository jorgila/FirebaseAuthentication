package com.estholon.firebaseauthentication.data.datasources.authentication.google

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.dtos.UserDto

interface GoogleAuthenticationDataSource {

    // GOOGLE
    suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?>
    suspend fun signInGoogle(activity: Activity) : Result<UserDto?>
    suspend fun handleCredentialResponse(result: GetCredentialResponse):Result<UserDto?>
    suspend fun clearCredentialState()
    suspend fun linkGoogle( activity: Activity) : Result<UserDto?>

}