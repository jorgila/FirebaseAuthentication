package com.estholon.firebaseauthentication.domain.usecases.authentication

import android.app.Activity
import com.estholon.firebaseauthentication.domain.models.AnalyticsModel
import com.estholon.firebaseauthentication.domain.repositories.AuthenticationRepository
import com.estholon.firebaseauthentication.domain.usecases.analytics.SendEventUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInGoogleUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
)  {

    suspend operator fun invoke(activity: Activity) : Result<Unit> {
        return try {
            withContext(Dispatchers.IO){
                val result = authenticationRepository.signInGoogle(activity)

                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Google", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Google", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(Exception(exception.message))
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}