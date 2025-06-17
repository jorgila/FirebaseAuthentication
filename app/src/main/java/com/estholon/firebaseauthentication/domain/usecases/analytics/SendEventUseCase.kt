package com.estholon.firebaseauthentication.domain.usecases.analytics

import com.estholon.firebaseauthentication.domain.models.AnalyticsModel
import com.estholon.firebaseauthentication.domain.repositories.AnalyticsRepository
import javax.inject.Inject

class SendEventUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke (analytics: AnalyticsModel) {
        analyticsRepository.sendEvent(analytics)
    }
}