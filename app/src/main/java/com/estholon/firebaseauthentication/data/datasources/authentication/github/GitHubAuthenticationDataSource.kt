package com.estholon.firebaseauthentication.data.datasources.authentication.github

import android.app.Activity
import com.estholon.firebaseauthentication.data.dtos.UserDto

interface GitHubAuthenticationDataSource {

    // GITHUB
    suspend fun signInGitHub(activity: Activity) : Result<UserDto?>
    suspend fun linkGitHub( activity: Activity) : Result<UserDto?>

}