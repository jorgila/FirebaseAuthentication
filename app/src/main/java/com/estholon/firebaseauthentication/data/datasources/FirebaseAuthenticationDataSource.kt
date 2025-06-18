package com.estholon.firebaseauthentication.data.datasources

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.managers.AuthRes
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userMapper: UserMapper
) : AuthenticationDataSource {

    private fun FirebaseUser.toUserDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        phoneNumber = phoneNumber
    )

    // EMAIL

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

    override suspend fun signInGoogle(idToken: String?): Result<UserDto?> {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        return completeRegisterWithCredential(credential)
    }

    override suspend fun signInFacebook(): Result<UserDto?> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithGitHub(): Result<UserDto?> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithMicrosoft(): Result<UserDto?> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithTwitter(): Result<UserDto?> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithYahoo(): Result<UserDto?> {
        TODO("Not yet implemented")
    }

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