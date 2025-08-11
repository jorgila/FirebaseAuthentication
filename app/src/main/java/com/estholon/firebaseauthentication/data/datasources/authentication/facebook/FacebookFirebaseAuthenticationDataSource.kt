package com.estholon.firebaseauthentication.data.datasources.authentication.facebook

import android.util.Log
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FacebookFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FacebookAuthenticationDataSource {

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    // SIGN IN FACEBOOK

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserDto?> {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        return completeRegisterWithCredential(credential)
    }

    override suspend fun linkFacebook(accessToken: AccessToken): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            // Create Facebook credencial with AccessToken
            val credential = FacebookAuthProvider.getCredential(accessToken.token)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Cuenta de Facebook vinculada exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Facebook"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Facebook", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    // ADDITIONAL FUNCTIONS

    private suspend fun completeRegisterWithCredential(credential: AuthCredential): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

}