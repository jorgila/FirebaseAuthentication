package com.estholon.firebaseauthentication.domain.usecases.analytics

import com.estholon.firebaseauthentication.domain.models.AnalyticsModel

interface SendEventUseCase {

    operator fun invoke(analytics: AnalyticsModel)

}