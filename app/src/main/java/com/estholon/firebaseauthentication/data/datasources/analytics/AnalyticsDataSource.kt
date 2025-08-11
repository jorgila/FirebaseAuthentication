package com.estholon.firebaseauthentication.data.datasources.analytics

import com.estholon.firebaseauthentication.data.dtos.AnalyticsDto

interface AnalyticsDataSource {
    fun sendEvent(analytics: AnalyticsDto)
}