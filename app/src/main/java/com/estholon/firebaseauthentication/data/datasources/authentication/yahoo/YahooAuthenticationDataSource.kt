package com.estholon.firebaseauthentication.data.datasources.authentication.yahoo

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto

interface YahooAuthenticationDataSource {

    // YAHOO
    suspend fun signInYahoo(activity: Activity) : Result<UserDto?>
    suspend fun linkYahoo( activity: Activity) : Result<UserDto?>

}