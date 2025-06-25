package com.estholon.firebaseauthentication.data.repositories

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.datasources.AuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userMapper: UserMapper
): AuthenticationRepository {

    override fun isUserLogged(): Boolean {
        return authenticationDataSource.isUserLogged()
    }

    override suspend fun signUpEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signUpEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signInEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInAnonymously(): Result<UserModel?> {
        return authenticationDataSource.signInAnonymously()
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInPhone(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        authenticationDataSource.signInPhone(phoneNumber,activity,callback)
    }

    override suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ): Result<UserModel?> {
        return authenticationDataSource.verifyCode(verificationCode, phoneCode)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInGoogleCredentialManager(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }


    override suspend fun signInGoogle(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInGoogle(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun handleCredentialResponse(result: GetCredentialResponse): Result<UserModel?> {
        return authenticationDataSource.handleCredentialResponse(result)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserModel?> {
        return authenticationDataSource.signInFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInGitHub(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInGitHub(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInMicrosoft(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInMicrosoft(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInTwitter(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInTwitter(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInYahoo(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInYahoo(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signOut() {
        authenticationDataSource.signOut()
    }

    override suspend fun resetPassword ( email : String) : Result<Unit> {
        return authenticationDataSource.resetPassword(email)
    }

    override suspend fun clearCredentialState() {
        return authenticationDataSource.clearCredentialState()
    }

}