package com.estholon.firebaseauthentication.domain.repositories

import com.estholon.firebaseauthentication.domain.models.AnalyticsModel

interface AnalyticsRepository {
    fun sendEvent(analytics: AnalyticsModel)
}