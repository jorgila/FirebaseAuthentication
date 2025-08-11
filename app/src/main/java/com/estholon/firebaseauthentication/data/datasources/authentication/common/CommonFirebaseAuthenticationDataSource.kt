package com.estholon.firebaseauthentication.data.datasources.authentication.common

import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CommonFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthenticationDataSource: GoogleAuthenticationDataSource
) : CommonAuthenticationDataSource {

    // USER STATUS

    private fun getCurrentUser() = firebaseAuth.currentUser

    override fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    // SIGN OUT OR LOGOUT

    override suspend fun signOut() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
        googleAuthenticationDataSource.clearCredentialState()
    }



}