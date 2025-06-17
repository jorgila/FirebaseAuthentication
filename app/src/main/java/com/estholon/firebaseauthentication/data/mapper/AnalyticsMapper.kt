package com.estholon.firebaseauthentication.data.mapper

import com.estholon.firebaseauthentication.data.dtos.AnalyticsDto
import com.estholon.firebaseauthentication.domain.models.AnalyticsModel

class AnalyticsMapper {

    fun analyticsDomainToDto(model: AnalyticsModel): AnalyticsDto {
        return AnalyticsDto(
            title = model.title,
            analyticsString = model.analyticsString,
            analyticsDouble = model.analyticsDouble,
            analyticsBundle = model.analyticsBundle,
            analyticsLong = model.analyticsLong,
            analyticsBundleArray = model.analyticsBundleArray
        )
    }

    fun analyticsDtoToDomain(dto: AnalyticsDto): AnalyticsModel {
        return AnalyticsModel(
            title = dto.title,
            analyticsString = dto.analyticsString,
            analyticsDouble = dto.analyticsDouble,
            analyticsBundle = dto.analyticsBundle,
            analyticsLong = dto.analyticsLong,
            analyticsBundleArray = dto.analyticsBundleArray
        )
    }

}