package com.estholon.firebaseauthentication.data.repositories

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.datasources.AuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userMapper: UserMapper
): AuthenticationRepository {

    // GENERAL FUNCTIONS

    override fun isUserLogged(): Boolean {
        return authenticationDataSource.isUserLogged()
    }

    // EMAIL

    override suspend fun signUpEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signUpEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signInEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.linkEmail(email, password)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN IN ANONYMOUSLY

    override suspend fun signInAnonymously(): Result<UserModel?> {
        return authenticationDataSource.signInAnonymously()
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN IN PHONE

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

    // SIGN IN GOOGLE

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

    override suspend fun clearCredentialState() {
        return authenticationDataSource.clearCredentialState()
    }

    override suspend fun linkGoogle(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.linkGoogle(activity)
            .map { dto -> dto?.let{ userMapper.userDtoToDomain(it) } }
    }

    // FACEBOOK

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserModel?> {
        return authenticationDataSource.signInFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkFacebook(accessToken: AccessToken): Result<UserModel?> {
        return authenticationDataSource.linkFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // GITHUB

    override suspend fun signInGitHub(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInGitHub(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // MICROSOFT

    override suspend fun signInMicrosoft(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInMicrosoft(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // TWITTER

    override suspend fun signInTwitter(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInTwitter(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // YAHOO

    override suspend fun signInYahoo(activity: Activity): Result<UserModel?> {
        return authenticationDataSource.signInYahoo(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN OUT

    override suspend fun signOut() {
        authenticationDataSource.signOut()
    }

    // RESET PASSWORD

    override suspend fun resetPassword ( email : String) : Result<Unit> {
        return authenticationDataSource.resetPassword(email)
    }

}