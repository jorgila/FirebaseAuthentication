package com.estholon.firebaseauthentication.data.managers

import com.estholon.firebaseauthentication.data.model.AnalyticModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

class AnalyticsManager @Inject constructor(private val analytics: FirebaseAnalytics){

    fun sendEvent(analyticModel: AnalyticModel){
        analytics.logEvent(analyticModel.title){
            analyticModel.analyticsString.map { param(it.first, it.second) }
            analyticModel.analyticsLong.map { param(it.first, it.second) }
            analyticModel.analyticsDouble.map { param(it.first, it.second) }
            analyticModel.analyticsBundle.map { param(it.first, it.second) }
            analyticModel.analyticsBundleArray.map { param(it.first, it.second.toTypedArray()) }
        }
    }
}