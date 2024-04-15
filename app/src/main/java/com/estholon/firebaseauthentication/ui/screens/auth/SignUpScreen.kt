package com.estholon.firebaseauthentication.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    lateinit var callbackManager: CallbackManager
    val googleLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode== Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    signUpViewModel.signUpWithGoogle(
                        idToken = account.idToken!!,
                        navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                        communicateError = {Toast.makeText(context,"Failed login",Toast.LENGTH_LONG).show()})
                } catch (e: ApiException){
                    Toast.makeText(context,"Ha ocurrido un error: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        SignInLink(onCreateAccount = { navController.navigate(Routes.SignInScreen.route) })
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication"
        )
        Spacer(modifier = Modifier.height(30.dp))
        SignUpByMail( onSignUpEmail = { user, password -> signUpViewModel.signUpEmail(
                    email = user,
                    password = password,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                    communicateError = { Toast.makeText(context,"Failed Sign Up",Toast.LENGTH_LONG).show() }
                )
            }
        )

        // Facebook

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(context,"Probamos otra red social?",Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(context,"Ha ocurrido un error: ${error.message}",Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(result: LoginResult) {
                    signUpViewModel.signUpWithFacebook(
                        result.accessToken,
                        navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                        communicateError = { Toast.makeText(context,"Failed login",Toast.LENGTH_LONG).show() }
                    )
                }

            })

        // Facebook End


        Spacer(modifier = Modifier.height(30.dp))
        OtherMethods(
            onAnonymously = { signUpViewModel.signUpAnonymously(
                navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                communicateError = { Toast.makeText(context,"Failed Sign Up", Toast.LENGTH_LONG).show() }
            )
            },
            onGoogleSignIn = {
                signUpViewModel.onGoogleSignUpSelected{
                    googleLauncher.launch(it.signInIntent)
                }
            },
            onFacebookSignIn = {
                LoginManager.getInstance()
                    .logInWithReadPermissions(context as ActivityResultRegistryOwner, callbackManager, listOf("email", "public_profile"))
            },
            onGitHubSignIn = {
              signUpViewModel.onOathLoginSelected(
                  oath = OathLogin.GitHub,
                  activity = activity,
                  navigateToHome = { navController.navigate(Routes.HomeScreen.route)},
                  communicateError = { Toast.makeText(context,"Failed login", Toast.LENGTH_LONG).show() }
              )
            },
            onMicrosoftSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Microsoft,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route)  },
                    communicateError = { Toast.makeText(context,"Failed login",Toast.LENGTH_LONG).show() }
                )
            },
            onTwitterSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Twitter,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                    communicateError = { Toast.makeText(context,"Failed login",Toast.LENGTH_LONG).show() }
                )
            },
            onYahooSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Yahoo,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route)},
                    communicateError = { Toast.makeText(context,"Failed login",Toast.LENGTH_LONG).show() }
                )
            }
        )
    }
}

@Composable
fun SignInLink(onCreateAccount: () -> Unit){
    Box(modifier = Modifier.width(300.dp), contentAlignment = Alignment.TopEnd){

        TextButton(
            onClick = {
                onCreateAccount()
            }
        ) {
            Text(
                text = "Iniciar sesión".uppercase()
            )
        }

    }
}
@Composable
fun SignUpByMail(onSignUpEmail: (user: String, password: String) -> Unit){

    var user by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    TextField(
        label = { Text(text="Usuario") },
        value = user,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        onValueChange = { user = it}
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        onValueChange = {password = it}
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                onSignUpEmail(user, password)
            },
            enabled = (user != null && password.length >= 6),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text(text = "Crear Cuenta".uppercase())
        }
    }
    Spacer(modifier = Modifier.height(60.dp))
}