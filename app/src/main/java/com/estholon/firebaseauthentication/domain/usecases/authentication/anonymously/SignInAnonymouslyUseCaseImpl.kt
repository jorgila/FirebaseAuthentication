package com.estholon.firebaseauthentication.domain.usecases.authentication.anonymously

import com.estholon.firebaseauthentication.domain.models.AnalyticsModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import javax.inject.Inject

class SignInAnonymouslyUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : SignInAnonymouslyUseCase {

    override suspend operator fun invoke() : Result<Unit> {
        return try {
                val result = authenticationRepository.signInAnonymously()

                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Anonymously", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Anonymously", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(Exception(exception.message))
                    }
                )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}