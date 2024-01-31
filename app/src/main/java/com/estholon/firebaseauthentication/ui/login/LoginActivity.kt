package com.estholon.firebaseauthentication.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.estholon.firebaseauthentication.databinding.ActivityLoginBinding
import com.estholon.firebaseauthentication.databinding.DialogPhoneLoginBinding
import com.estholon.firebaseauthentication.ui.detail.DetailActivity
import com.estholon.firebaseauthentication.ui.signup.SignUpActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // CONNECTIONS
    //// ViewModel
    private val viewModel: LoginViewModel by viewModels()

    // BINDING
    private lateinit var binding: ActivityLoginBinding

    // VARIABLES
    private lateinit var callbackManager: CallbackManager

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode== Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    viewModel.signInWithGoogle(account.idToken!!){
                        navigateToDetail()
                    }
                } catch (e:ApiException){
                    Toast.makeText(this,"Ha ocurrido un error: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        binding.btnGoogle.setOnClickListener {
            viewModel.onGoogleSignInSelected{
                googleLauncher.launch(it.signInIntent)
            }

        }

        binding.btnGitHub.setOnClickListener {
            viewModel.onGitHubSignInSelected(this){ navigateToDetail() }
        }

        binding.btnMicrosoft.setOnClickListener {
            viewModel.onMicrosoftSignInSelected(this){ navigateToDetail() }
        }


        // Facebook

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    showToast("Probamos otra red social?")
                }

                override fun onError(error: FacebookException) {
                    showToast("Ha ocurrido un error: ${error.message}")
                }

                override fun onSuccess(result: LoginResult) {
                    viewModel.signInWithFacebook(result.accessToken) { navigateToDetail() }
                }

            })

        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, callbackManager, listOf("email", "public_profile"))
        }

        // Facebook End

    }

    private fun showPhoneLogin(){
        val phoneBinding = DialogPhoneLoginBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).apply{setView(phoneBinding.root)}.create()

        phoneBinding.btnPhone.setOnClickListener{
            viewModel.loginWithPhone(
                phoneBinding.tiePhone.text.toString(),
                this,
                onCodeSent={
                    phoneBinding.tiePhone.isEnabled = false
                    phoneBinding.btnPhone.isEnabled = false
                    phoneBinding.pinView.isVisible = true
                    phoneBinding.pinView.requestFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(phoneBinding.pinView, InputMethodManager.SHOW_IMPLICIT)
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