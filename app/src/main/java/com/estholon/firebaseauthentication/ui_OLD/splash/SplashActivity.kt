package com.estholon.firebaseauthentication.ui_OLD.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.estholon.firebaseauthentication.databinding.ActivitySplashBinding
import com.estholon.firebaseauthentication.ui_OLD.detail.DetailActivity
import com.estholon.firebaseauthentication.ui_OLD.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    // CONNECTIONS
    //// ViewModel
    private val viewModel : SplashViewModel by viewModels()

    //BINDING
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(viewModel.checkDestination()){
            SplashDestination.Home -> navigateToHome()
            SplashDestination.Login -> navigateToLogin()
        }

    }

    private fun navigateToHome() {
        startActivity(Intent(this, DetailActivity::class.java))
    }

    private fun navigateToLogin(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
}