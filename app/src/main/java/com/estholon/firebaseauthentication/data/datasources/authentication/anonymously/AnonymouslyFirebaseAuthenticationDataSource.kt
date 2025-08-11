package com.estholon.firebaseauthentication.data.datasources.authentication.anonymously

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.toUserDto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AnonymouslyFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AnonymouslyAuthenticationDataSource {
    // ANONYMOUSLY

    //// SIGN IN & SIGN UP

    override suspend fun signInAnonymously(): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception("Error al iniciar sesión"))
                    cancellableContinuation.resume(result)
                }
        }

    }
}