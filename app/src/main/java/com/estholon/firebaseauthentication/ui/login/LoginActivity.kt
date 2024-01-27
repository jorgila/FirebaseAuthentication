package com.estholon.firebaseauthentication.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.databinding.ActivityLoginBinding
import com.estholon.firebaseauthentication.databinding.DialogPhoneLoginBinding
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
    private val viewModel: LoginViewModel by viewModels()

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
                viewModel.isLoading.collect{
                    binding.pb.isVisible = it
                }
            }
        }
    }

    private fun initListeners() {
        binding.btn.setOnClickListener{
            viewModel.login(
                user = binding.tieUser.text.toString(),
                password = binding.tiePassword.text.toString()
            ){ navigateToDetail()}
        }
        binding.tvSignUp.setOnClickListener{
            navigateToSignUp()
        }
        binding.btnMobile.setOnClickListener{
            showPhoneLogin()
        }
    }

    private fun showPhoneLogin(){
        val phoneBinding = DialogPhoneLoginBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).apply{setView(phoneBinding.root)}.create()

        phoneBinding.btnPhone.setOnClickListener{
            viewModel.loginWithPhone(
                phoneBinding.tiePhone.text.toString(),
                this,
                onCodeSent={
                           phoneBinding.pinView.isVisible = true
                },
                onVerificationComplete={navigateToDetail()},
                onVerificationFailed={showToast("Ha habido un error:$it")}
            )
        }

        phoneBinding.pinView.doOnTextChanged { text, _, _, _ ->
            if(text?.length == 6){
                viewModel.verifyCode(text.toString()){navigateToDetail()}
            }
        }

        alertDialog.show()
    }

    private fun showToast(msg: String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(){
        startActivity(Intent(this, DetailActivity::class.java))
    }

    private fun navigateToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

}