package com.estholon.firebaseauthentication.data.datasources.authentication.github

import android.app.Activity
import android.util.Log
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GitHubFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GitHubAuthenticationDataSource {

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    // SIGN IN GITHUB

    override suspend fun signInGitHub(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = listOf("user:email")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH GITHUB
    override suspend fun linkGitHub(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("github.com").apply {
                scopes = listOf("user:email")
            }.build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "GitHub vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de GitHub"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con GitHub", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    private suspend fun initRegisterWithProvider(activity: Activity, provider: OAuthProvider) : Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.pendingAuthResult
                ?.addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                ?.addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }?: completeRegisterWithProvider(activity, provider,cancellableContinuation)
        }
    }

    private fun completeRegisterWithProvider(
        activity: Activity,
        provider: OAuthProvider,
        cancellableContinuation: CancellableContinuation<Result<UserDto?>>
    ) {
        firebaseAuth.startActivityForSignInWithProvider(activity, provider)
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