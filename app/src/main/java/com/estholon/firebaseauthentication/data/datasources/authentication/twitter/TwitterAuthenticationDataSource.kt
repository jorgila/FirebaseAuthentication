package com.estholon.firebaseauthentication.data.datasources.authentication.twitter

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto

interface TwitterAuthenticationDataSource {

    // TWITTER
    suspend fun signInTwitter(activity: Activity) : Result<UserDto?>
    suspend fun linkTwitter( activity: Activity) : Result<UserDto?>

}