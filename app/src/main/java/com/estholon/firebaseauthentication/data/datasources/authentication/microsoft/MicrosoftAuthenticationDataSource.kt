package com.estholon.firebaseauthentication.data.datasources.authentication.microsoft

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto

interface MicrosoftAuthenticationDataSource {

    // MICROSOFT
    suspend fun signInMicrosoft(activity: Activity) : Result<UserDto?>
    suspend fun linkMicrosoft( activity: Activity) : Result<UserDto?>

}