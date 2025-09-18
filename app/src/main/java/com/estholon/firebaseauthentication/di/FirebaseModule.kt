package com.estholon.firebaseauthentication.di

import com.estholon.firebaseauthentication.data.datasources.analytics.AnalyticsDataSource
import com.estholon.firebaseauthentication.data.datasources.analytics.FirebaseAnalyticsDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.anonymously.AnonymouslyAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.anonymously.AnonymouslyFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.common.CommonAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.common.CommonFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.email.EmailAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.email.EmailFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.facebook.FacebookAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.facebook.FacebookFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.github.GitHubAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.github.GitHubFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.microsoft.MicrosoftAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.microsoft.MicrosoftFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.multifactor.MultifactorAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.multifactor.MultifactorFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.phone.PhoneAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.phone.PhoneFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.twitter.TwitterAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.twitter.TwitterFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.yahoo.YahooAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.yahoo.YahooFirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.AnalyticsMapper
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.data.repositories.AnalyticsRepositoryImpl
import com.estholon.firebaseauthentication.data.repositories.AuthenticationRepositoryImpl
import com.estholon.firebaseauthentication.domain.repositories.AnalyticsRepository
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously.SignInAnonymouslyUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously.SignInAnonymouslyUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.common.SignOutUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.common.SignOutUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsEmailValidUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.IsPasswordValidUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.LinkEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.LinkEmailUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.ResetPasswordUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.ResetPasswordUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignInEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignInEmailUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignUpEmailUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.email.SignUpEmailUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.LinkFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.LinkFacebookUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.SignInFacebookUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.facebook.SignInFacebookUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.LinkGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.LinkGitHubUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.github.SignInGitHubUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.ClearCredentialStateUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.ClearCredentialStateUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.LinkGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.LinkGoogleUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleCredentialManagerUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleCredentialManagerUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.google.SignInGoogleUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.LinkMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.LinkMicrosoftUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.SignInMicrosoftUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.microsoft.SignInMicrosoftUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor.StartEnrollPhoneUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.multifactor.StartEnrollPhoneUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.LinkTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.LinkTwitterUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.SignInTwitterUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.twitter.SignInTwitterUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.LinkYahooUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.LinkYahooUseCaseImpl
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.SignInYahooUseCase
import com.estholon.firebaseauthentication.domain.usecases.authentication.yahoo.SignInYahooUseCaseImpl
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Binds
    @Singleton
    abstract fun bindCommonAuthenticationDataSource(
        commonFirebaseAuthenticationDataSource: CommonFirebaseAuthenticationDataSource
    ): CommonAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindAnonymouslyAuthenticationDataSource(
        anonymouslyFirebaseAuthenticationDataSource: AnonymouslyFirebaseAuthenticationDataSource
    ): AnonymouslyAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindEmailAuthenticationDataSource(
        emailFirebaseAuthenticationDataSource: EmailFirebaseAuthenticationDataSource
    ): EmailAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindGoogleAuthenticationDataSource(
        googleFirebaseAuthenticationDataSource: GoogleFirebaseAuthenticationDataSource
    ): GoogleAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindFacebookAuthenticationDataSource(
        facebookFirebaseAuthenticationDataSource: FacebookFirebaseAuthenticationDataSource
    ): FacebookAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindGitHubAuthenticationDataSource(
        gitHubFirebaseAuthenticationDataSource: GitHubFirebaseAuthenticationDataSource
    ): GitHubAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindMicrosoftAuthenticationDataSource(
        microsoftFirebaseAuthenticationDataSource: MicrosoftFirebaseAuthenticationDataSource
    ): MicrosoftAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindTwitterAuthenticationDataSource(
        twitterFirebaseAuthenticationDataSource: TwitterFirebaseAuthenticationDataSource
    ): TwitterAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindYahooAuthenticationDataSource(
        yahooFirebaseAuthenticationDataSource: YahooFirebaseAuthenticationDataSource
    ): YahooAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindPhoneAuthenticationDataSource(
        phoneFirebaseAuthenticationDataSource: PhoneFirebaseAuthenticationDataSource
    ): PhoneAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindMultifactorAuthenticationDataSource(
        multifactorFirebaseAuthenticationDataSource: MultifactorFirebaseAuthenticationDataSource
    ): MultifactorAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsDataSource(
        firebaseAnalyticsDataSource: FirebaseAnalyticsDataSource
    ): AnalyticsDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        analyticsRepositoryImpl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    // USE CASES

    @Binds
    @Singleton
    abstract fun provideStartEnrollPhoneUseCase(
        startEnrollPhoneUseCaseImpl: StartEnrollPhoneUseCaseImpl
    ): StartEnrollPhoneUseCase

    @Binds
    @Singleton
    abstract fun provideSendEventUseCase(
        sendEventUseCaseImpl: SendEventUseCaseImpl
    ): SendEventUseCase

    @Binds
    @Singleton
    abstract fun provideSignInAnonymouslyUseCase(
        signInAnonymouslyUseCaseImpl:  SignInAnonymouslyUseCaseImpl
    ): SignInAnonymouslyUseCase

    @Binds
    @Singleton
    abstract fun provideSignOutUseCase(
        signOutUseCaseImpl:  SignOutUseCaseImpl
    ): SignOutUseCase

    @Binds
    @Singleton
    abstract fun provideIsEmailValidUseCase(
        isEmailValidUseCaseImpl:  IsEmailValidUseCaseImpl
    ): IsEmailValidUseCase

    @Binds
    @Singleton
    abstract fun provideIsPasswordValidUseCase(
        isPasswordValidUseCaseImpl: IsPasswordValidUseCaseImpl
    ): IsPasswordValidUseCase

    @Binds
    @Singleton
    abstract fun provideLinkEmailUseCase(
        linkEmailUseCaseImpl: LinkEmailUseCaseImpl
    ): LinkEmailUseCase

    @Binds
    @Singleton
    abstract fun provideResetPasswordUseCase(
        resetPasswordUseCaseImpl: ResetPasswordUseCaseImpl
    ): ResetPasswordUseCase

    @Binds
    @Singleton
    abstract fun provideSignInEmailUseCase(
        signInEmailUseCaseImpl: SignInEmailUseCaseImpl
    ): SignInEmailUseCase

    @Binds
    @Singleton
    abstract fun provideSignUpEmailUseCase(
        signUpEmailUseCaseImpl: SignUpEmailUseCaseImpl
    ): SignUpEmailUseCase

    @Binds
    @Singleton
    abstract fun provideLinkFacebookUseCase(
        linkFacebookUseCaseImpl: LinkFacebookUseCaseImpl
    ): LinkFacebookUseCase

    @Binds
    @Singleton
    abstract fun provideSignInFacebookUseCase(
        signInFacebookUseCaseImpl: SignInFacebookUseCaseImpl
    ): SignInFacebookUseCase

    @Binds
    @Singleton
    abstract fun provideLinkGitHubUseCase(
        linkGitHubUseCaseImpl: LinkGitHubUseCaseImpl
    ): LinkGitHubUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGitHubUseCase(
        signInGitHubUseCaseImpl: SignInGitHubUseCaseImpl
    ): SignInGitHubUseCase

    @Binds
    @Singleton
    abstract fun provideClearCredentialStateUseCase(
        clearCredentialStateUseCaseImpl: ClearCredentialStateUseCaseImpl
    ): ClearCredentialStateUseCase

    @Binds
    @Singleton
    abstract fun provideLinkGoogleUseCase(
        linkGoogleUseCaseImpl: LinkGoogleUseCaseImpl
    ): LinkGoogleUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGoogleCredentialManagerUseCase(
        signInGoogleCredentialManagerUseCaseImpl: SignInGoogleCredentialManagerUseCaseImpl
    ): SignInGoogleCredentialManagerUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGoogleUseCase(
        signInGoogleUseCaseImpl: SignInGoogleUseCaseImpl
    ): SignInGoogleUseCase

    @Binds
    @Singleton
    abstract fun provideLinkMicrosoftUseCase(
        linkMicrosoftUseCaseImpl: LinkMicrosoftUseCaseImpl
    ): LinkMicrosoftUseCase

    @Binds
    @Singleton
    abstract fun provideSignInMicrosoftUseCase(
        signInMicrosoftUseCaseImpl: SignInMicrosoftUseCaseImpl
    ): SignInMicrosoftUseCase

    @Binds
    @Singleton
    abstract fun provideLinkTwitterUseCase(
        linkTwitterUseCaseImpl: LinkTwitterUseCaseImpl
    ): LinkTwitterUseCase

    @Binds
    @Singleton
    abstract fun provideSignInTwitterUseCase(
        linkSignInTwitterUseCaseImpl: SignInTwitterUseCaseImpl
    ): SignInTwitterUseCase

    @Binds
    @Singleton
    abstract fun provideLinkYahooUseCase(
        linkYahooUseCaseImpl: LinkYahooUseCaseImpl
    ): LinkYahooUseCase

    @Binds
    @Singleton
    abstract fun provideSignInYahooUseCase(
        signInYahooUseCaseImpl: SignInYahooUseCaseImpl
    ): SignInYahooUseCase


    companion object {

        @Singleton
        @Provides
        fun provideFirebaseAuth() = FirebaseAuth.getInstance()

        @Singleton
        @Provides
        fun provideFirebaseAnalytics() = Firebase.analytics

        @Provides
        @Singleton
        fun provideUserMapper(): UserMapper = UserMapper()

        @Provides
        @Singleton
        fun provideAnalyticsMapper() : AnalyticsMapper = AnalyticsMapper()

    }


}