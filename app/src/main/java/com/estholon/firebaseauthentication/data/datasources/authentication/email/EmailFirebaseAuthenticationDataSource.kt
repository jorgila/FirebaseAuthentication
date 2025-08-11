package com.estholon.firebaseauthentication.data.datasources.authentication.email

import android.content.Context
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class EmailFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userMapper: UserMapper,
    @ApplicationContext private val context: Context
) : EmailAuthenticationDataSource {

//// SIGN UP

    override suspend fun signUpEmail(email: String, password: String) : Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
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

//// SIGN IN

    override suspend fun signInEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithEmailAndPassword(email,password)
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

//// LINK

    override suspend fun linkEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                val result = Result.failure<UserDto?>(Exception("No hay usuario autenticado"))
                cancellableContinuation.resume(result)
                return@suspendCancellableCoroutine
            }

            // Create credential of email and password
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de email"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

//// RESET PASSWORD

    override suspend fun resetPassword( email: String ): Result<Unit> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    val result = Result.success(Unit)
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<Unit>(Exception(it.message ?: "Error al restablecer la contraseña"))
                    cancellableContinuation.resume(result)
                }
        }
    }

}