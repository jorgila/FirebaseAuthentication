package com.estholon.firebaseauthentication.data.datasources.authentication.microsoft

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

class MicrosoftFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : MicrosoftAuthenticationDataSource {

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    // SIGN IN MICROSOFT

    override suspend fun signInMicrosoft(activity: Activity): Result<UserDto?> {
        val provider = OAuthProvider.newBuilder("microsoft.com").apply{
            scopes = listOf("mail.read","calendars.read")
        }.build()
        return initRegisterWithProvider(activity,provider)
    }

    // LINK WITH MICROSOFT
    override suspend fun linkMicrosoft(activity: Activity): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            if (isProviderAlreadyLinked("microsoft.com")) {
                val result = Result.failure<UserDto?>(Exception("Ya tienes una cuenta de Microsoft vinculada"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            val provider = OAuthProvider.newBuilder("microsoft.com").apply {
                scopes = listOf("mail.read", "calendars.read")
            }.build()

            currentUser.startActivityForLinkWithProvider(activity, provider)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Microsoft vinculado exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Microsoft"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Microsoft", exception)

                    // Manejo específico de errores comunes
                    val errorMessage = when {
                        exception.message?.contains("already in use") == true ->
                            "Esta cuenta de Microsoft ya está vinculada a otro usuario"
                        exception.message?.contains("credential-already-in-use") == true ->
                            "Estas credenciales de Microsoft ya están siendo usadas"
                        exception.message?.contains("provider-already-linked") == true ->
                            "Ya tienes una cuenta de Microsoft vinculada"
                        exception.message?.contains("cancelled") == true ->
                            "Operación cancelada por el usuario"
                        else -> exception.message ?: "Error desconocido al vincular cuenta de Microsoft"
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