package com.estholon.firebaseauthentication.data.datasources.authentication.common

import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommonFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthenticationDataSource: GoogleAuthenticationDataSource
) : CommonAuthenticationDataSource {

    // USER STATUS

    private fun getCurrentUser() = firebaseAuth.currentUser

    override fun isUserLogged(): Result<Boolean> {
        return try {
            Result.success(getCurrentUser() != null)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                Result.success(false)
            } else {
                // Force token renovation
                currentUser.getIdToken(true).await()
                // Reload user to get the updated status
                currentUser.reload().await()
                // Get verification status
                val isEmailVerified = currentUser.isEmailVerified
                Result.success(isEmailVerified)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // EMAIL VERIFICATION

    override suspend fun sendEmailVerification(): Result<Unit> = runCatching {
        val user = firebaseAuth.currentUser ?: throw Exception("No hay usuario autenticado")
        user.sendEmailVerification().await()
    }

    // SIGN OUT OR LOGOUT

    override suspend fun signOut() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
        googleAuthenticationDataSource.clearCredentialState()
    }



}