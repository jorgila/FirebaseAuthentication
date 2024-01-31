package com.estholon.firebaseauthentication.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estholon.firebaseauthentication.data.AuthService
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthService): ViewModel(){

    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    lateinit var verificationCode: String

    fun login(user: String, password: String, navigateToDetail:()-> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = withContext(Dispatchers.IO){
                authService.login(user,password)
            }

            if(result!=null){
                navigateToDetail()
            }

            _isLoading.value = false
        }
    }


    fun loginWithPhone(
        phoneNumber:String,
        activity: Activity,
        onCodeSent:()->Unit,
        onVerificationComplete:()->Unit,
        onVerificationFailed:(String)->Unit){
        viewModelScope.launch{
            _isLoading.value=true

            val callback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(credentials: PhoneAuthCredential){

                    viewModelScope.launch {
                        val result = withContext(Dispatchers.IO){
                            authService.completeRegisterWithPhoneVerification(
                                credentials
                            )
                        }

                        if(result!=null){
                            onVerificationComplete()
                        }

                    }

                }

                override fun onVerificationFailed(p0: FirebaseException){
                    _isLoading.value=false
                    onVerificationFailed(p0.message.orEmpty())
                }

                override fun onCodeSent(verificationCode:String,p1:PhoneAuthProvider.ForceResendingToken){
                    this@LoginViewModel.verificationCode = verificationCode
                    _isLoading.value=false
                    onCodeSent()
                }

            }

            withContext(Dispatchers.IO){
                authService.loginWithPhone(phoneNumber,activity,callback)
            }

            _isLoading.value=false
        }
    }

    fun verifyCode(phoneCode: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                authService.verifyCode(verificationCode,phoneCode)
            }

            if(result!=null){
                onSuccessVerification()
            }
        }
    }

    fun onGoogleSignInSelected(googleLauncherSignIn:(GoogleSignInClient)->Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherSignIn(gsc)
    }

    fun signInWithGoogle(idToken: String?, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                authService.signInWithGoogle(idToken)
            }

            if(result !=null){
                navigateToDetail()
            }
        }
    }

    fun signInWithFacebook(accessToken: AccessToken, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.signInWithFacebook(accessToken)
            }

            if (result != null) {
                navigateToDetail()
            }
        }
    }

    fun onGitHubSignInSelected(activity: Activity, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                authService.signInWithGitHub(activity)
            }

            if (result != null) {
                navigateToDetail()
            }

        }

    }

}