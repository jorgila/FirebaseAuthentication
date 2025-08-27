package com.estholon.firebaseauthentication.data.repositories

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.firebaseauthentication.data.datasources.authentication.anonymously.AnonymouslyAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.common.CommonAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.email.EmailAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.facebook.FacebookAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.github.GitHubAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.microsoft.MicrosoftAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.multifactor.MultifactorAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.phone.PhoneAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.twitter.TwitterAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.yahoo.YahooAuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val commonAuthenticationDataSource: CommonAuthenticationDataSource,
    private val anonymouslyAuthenticationDataSource: AnonymouslyAuthenticationDataSource,
    private val emailAuthenticationDataSource: EmailAuthenticationDataSource,
    private val googleAuthenticationDataSource: GoogleAuthenticationDataSource,
    private val facebookAuthenticationDataSource: FacebookAuthenticationDataSource,
    private val gitHubAuthenticationDataSource: GitHubAuthenticationDataSource,
    private val microsoftAuthenticationDataSource: MicrosoftAuthenticationDataSource,
    private val phoneAuthenticationDataSource: PhoneAuthenticationDataSource,
    private val twitterAuthenticationDataSource: TwitterAuthenticationDataSource,
    private val yahooAuthenticationDataSource: YahooAuthenticationDataSource,
    private val multifactorAuthenticationDataSource: MultifactorAuthenticationDataSource,
    private val userMapper: UserMapper
): AuthenticationRepository {

    // GENERAL FUNCTIONS

    override fun isUserLogged(): Boolean {
        return commonAuthenticationDataSource.isUserLogged()
    }

    // EMAIL

    override suspend fun signUpEmail(email: String, password: String): Result<UserModel?> {
        return emailAuthenticationDataSource.signUpEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInEmail(email: String, password: String): Result<UserModel?> {
        return emailAuthenticationDataSource.signInEmail( email, password )
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkEmail(email: String, password: String): Result<UserModel?> {
        return emailAuthenticationDataSource.linkEmail(email, password)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN IN ANONYMOUSLY

    override suspend fun signInAnonymously(): Result<UserModel?> {
        return anonymouslyAuthenticationDataSource.signInAnonymously()
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN IN PHONE

    override suspend fun signInPhone(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        phoneAuthenticationDataSource.signInPhone(phoneNumber,activity,callback)
    }

    override suspend fun verifyCode(
        verificationCode: String,
        phoneCode: String
    ): Result<UserModel?> {
        return phoneAuthenticationDataSource.verifyCode(verificationCode, phoneCode)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN IN GOOGLE

    override suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserModel?> {
        return googleAuthenticationDataSource.signInGoogleCredentialManager(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }


    override suspend fun signInGoogle(activity: Activity): Result<UserModel?> {
        return googleAuthenticationDataSource.signInGoogle(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun handleCredentialResponse(result: GetCredentialResponse): Result<UserModel?> {
        return googleAuthenticationDataSource.handleCredentialResponse(result)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun clearCredentialState() {
        return googleAuthenticationDataSource.clearCredentialState()
    }

    override suspend fun linkGoogle(activity: Activity): Result<UserModel?> {
        return googleAuthenticationDataSource.linkGoogle(activity)
            .map { dto -> dto?.let{ userMapper.userDtoToDomain(it) } }
    }

    // FACEBOOK

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserModel?> {
        return facebookAuthenticationDataSource.signInFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkFacebook(accessToken: AccessToken): Result<UserModel?> {
        return facebookAuthenticationDataSource.linkFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // GITHUB

    override suspend fun signInGitHub(activity: Activity): Result<UserModel?> {
        return gitHubAuthenticationDataSource.signInGitHub(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkGitHub(activity: Activity): Result<UserModel?> {
        return gitHubAuthenticationDataSource.linkGitHub(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // MICROSOFT

    override suspend fun signInMicrosoft(activity: Activity): Result<UserModel?> {
        return microsoftAuthenticationDataSource.signInMicrosoft(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkMicrosoft(activity: Activity): Result<UserModel?> {
        return microsoftAuthenticationDataSource.linkMicrosoft(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // TWITTER

    override suspend fun signInTwitter(activity: Activity): Result<UserModel?> {
        return twitterAuthenticationDataSource.signInTwitter(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkTwitter(activity: Activity): Result<UserModel?> {
        return twitterAuthenticationDataSource.linkTwitter(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // YAHOO

    override suspend fun signInYahoo(activity: Activity): Result<UserModel?> {
        return yahooAuthenticationDataSource.signInYahoo(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun linkYahoo(activity: Activity): Result<UserModel?> {
        return yahooAuthenticationDataSource.linkYahoo(activity)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    // SIGN OUT

    override suspend fun signOut() {
        commonAuthenticationDataSource.signOut()
    }

    // RESET PASSWORD

    override suspend fun resetPassword ( email : String) : Result<Unit> {
        return emailAuthenticationDataSource.resetPassword(email)
    }

    // MFA

    override suspend fun getMultifactorSession(): MultiFactorSession {
        return multifactorAuthenticationDataSource.getMultifactorSession()
    }

    override suspend fun enrollMfaSendSms(
        session: MultiFactorSession,
        phoneNumber: String
    ): Result<String> {
        return multifactorAuthenticationDataSource.enrollMfaSendSms(session, phoneNumber)
    }

}