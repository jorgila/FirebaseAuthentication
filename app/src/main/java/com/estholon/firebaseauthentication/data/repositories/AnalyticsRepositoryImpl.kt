package com.estholon.firebaseauthentication.data.repositories

import com.estholon.firebaseauthentication.data.datasources.analytics.AnalyticsDataSource
import com.estholon.firebaseauthentication.data.mapper.AnalyticsMapper
import com.estholon.firebaseauthentication.domain.models.AnalyticsModel
import com.estholon.firebaseauthentication.domain.repositories.AnalyticsRepository
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDataSource: AnalyticsDataSource,
    private val analyticsMapper: AnalyticsMapper
) : AnalyticsRepository {

    override fun sendEvent(analytics: AnalyticsModel) {
        val analyticsDto = analyticsMapper.analyticsDomainToDto(analytics)
        analyticsDataSource.sendEvent(analyticsDto)
    }

}