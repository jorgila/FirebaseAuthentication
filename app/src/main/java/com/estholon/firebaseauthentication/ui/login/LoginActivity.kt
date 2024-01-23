package com.estholon.firebaseauthentication.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.databinding.ActivityLoginBinding
import com.estholon.firebaseauthentication.ui.detail.DetailActivity
import com.estholon.firebaseauthentication.ui.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // CONNECTIONS
    //// ViewModel
    private val loginViewModel: LoginViewModel by viewModels()

    // BINDING
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                loginViewModel.isLoading.collect{
                    binding.pb.isVisible = it
                }
            }
        }
    }

    private fun initListeners() {
        binding.btn.setOnClickListener{
            loginViewModel.login(
                user = binding.tieUser.text.toString(),
                password = binding.tiePassword.text.toString()
            ){ navigateToDetail()}
        }
        binding.tvSignUp.setOnClickListener{
            navigateToSignUp()
        }
    }

    fun navigateToDetail(){
        startActivity(Intent(this, DetailActivity::class.java))
    }

    private fun navigateToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

}