package com.estholon.firebaseauthentication

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltAndroidApp
class FirebaseAuthenticationApp : Application(){
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase when activity is created
        FirebaseApp.initializeApp(this)



    }

}