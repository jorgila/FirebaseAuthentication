package com.estholon.firebaseauthentication.data.repositories

import com.estholon.firebaseauthentication.data.datasources.AuthenticationDataSource
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userMapper: UserMapper
): AuthenticationRepository {

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

    override suspend fun signInGoogle(idToken: String?): Result<UserModel?> {
        return authenticationDataSource.signInGoogle(idToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInFacebook(accessToken: AccessToken): Result<UserModel?> {
        return authenticationDataSource.signInFacebook(accessToken)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

}