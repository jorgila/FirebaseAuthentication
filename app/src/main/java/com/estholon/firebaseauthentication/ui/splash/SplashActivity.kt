package com.estholon.firebaseauthentication.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.databinding.ActivitySplashBinding
import com.estholon.firebaseauthentication.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    // CONNECTIONS
    //// ViewModel
    private val splashViewModel : SplashViewModel by viewModels()

    //BINDING
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToLogin()
    }

    fun navigateToLogin(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
}