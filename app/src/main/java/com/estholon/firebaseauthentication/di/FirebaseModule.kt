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