// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    // HILT
    alias(libs.plugins.hilt) apply false
    // KSP
    alias(libs.plugins.ksp) apply false
    // GOOGLE SERVICES
    alias(libs.plugins.google.services) apply false
    // FIREBASE
    alias(libs.plugins.crashlytics) apply false
    // COMPOSE COMPILERD
    alias(libs.plugins.compose.compiler) apply false
}