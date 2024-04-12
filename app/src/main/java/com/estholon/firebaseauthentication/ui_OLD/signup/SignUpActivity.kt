package com.estholon.firebaseauthentication.ui_OLD.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.estholon.firebaseauthentication.databinding.ActivitySignUpBinding
import com.estholon.firebaseauthentication.ui_OLD.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    // CONNECTIONS
    //// ViewModel
    private val viewModel: SignUpViewModel by viewModels()

    // BINDING
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initListeners() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.isLoading.collect{
                    binding.pb.isVisible = it
                }
            }
        }
    }

    private fun initUIState() {
        binding.btn.setOnClickListener {
            viewModel.register(
                email = binding.tieUser.text.toString(),
                password = binding.tiePassword.text.toString()
            ){ navigateToDetail()}
        }
    }

    private fun navigateToDetail() {
        startActivity(Intent(this,DetailActivity::class.java))
    }


}