package com.estholon.firebaseauthentication

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FirebaseAuthenticationApp : Application(){
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase when activity is created
        FirebaseApp.initializeApp(this)



    }

}