package com.estholon.firebaseauthentication.data.datasources.authentication.twitter

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

class TwitterFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : TwitterAuthenticationDataSource {

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    // SIGN IN TWITTER

    override suspend fun signInTwitter(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("twitter.com").build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH TWITTER
    override suspend fun linkTwitter(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            if (isProviderAlreadyLinked("twitter.com")) {
                val result = Result.failure<UserDto?>(Exception("Ya tienes una cuenta de Twitter vinculada"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("twitter.com").build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Twitter vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Twitter"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Twitter", exception)

                    // Manejo específico de errores comunes
                    val errorMessage = when {
                        exception.message?.contains("already in use") == true ->
                            "Esta cuenta de Twitter ya está vinculada a otro usuario"
                        exception.message?.contains("credential-already-in-use") == true ->
                            "Estas credenciales de Twitter ya están siendo usadas"
                        exception.message?.contains("provider-already-linked") == true ->
                            "Ya tienes una cuenta de Twitter vinculada"
                        exception.message?.contains("cancelled") == true ->
                            "Operación cancelada por el usuario"
                        exception.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet e intenta nuevamente"
                        else -> exception.message ?: "Error desconocido al vincular cuenta de Twitter"
                    }

                    val result = Result.failure<UserDto?>(Exception(errorMessage))
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


    private fun isProviderAlreadyLinked(providerId: String): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.providerData?.any { it.providerId == providerId } ?: false
    }

}