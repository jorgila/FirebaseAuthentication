package com.estholon.firebaseauthentication.di

import com.estholon.firebaseauthentication.data.datasources.AnalyticsDataSource
import com.estholon.firebaseauthentication.data.datasources.AuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.FirebaseAnalyticsDataSource
import com.estholon.firebaseauthentication.data.datasources.FirebaseAuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.AnalyticsMapper
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.data.repositories.AnalyticsRepositoryImpl
import com.estholon.firebaseauthentication.data.repositories.AuthenticationRepositoryImpl
import com.estholon.firebaseauthentication.domain.repositories.AnalyticsRepository
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
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
    abstract fun bindAuthenticationDataSource(
        firebaseAuthDataSource: FirebaseAuthenticationDataSource
    ): AuthenticationDataSource

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