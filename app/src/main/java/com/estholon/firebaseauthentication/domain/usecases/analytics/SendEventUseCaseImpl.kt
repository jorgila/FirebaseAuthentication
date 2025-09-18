package com.estholon.firebaseauthentication.domain.usecases.analytics

import com.estholon.firebaseauthentication.domain.models.AnalyticsModel
import com.estholon.firebaseauthentication.domain.repositories.AnalyticsRepository
import javax.inject.Inject

class SendEventUseCaseImpl @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : SendEventUseCase {
    override operator fun invoke (analytics: AnalyticsModel) {
        analyticsRepository.sendEvent(analytics)
    }
}