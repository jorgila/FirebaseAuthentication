package com.estholon.firebaseauthentication.ui.screens.authentication

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes
import com.estholon.firebaseauthentication.ui.navigation.Routes.HomeScreen
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    signUpViewModel.signUpFacebook(
                        result.accessToken,
                        navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                    )
                }

            })

        // Facebook End


        Spacer(modifier = Modifier.height(30.dp))
        OtherMethods(
            onPhoneSignIn = {}, //TODO
            onAnonymously = { signUpViewModel.signUpAnonymously(
                navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
            )
            },
            onGoogleSignIn = {
                signUpViewModel.signInGoogle(
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route) }
                )
            },
            onFacebookSignIn = {
                LoginManager.getInstance()
                    .logInWithReadPermissions(context as ActivityResultRegistryOwner, callbackManager, listOf("email", "public_profile"))
            },
            onGitHubSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.GitHub,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route)}
                )
            },
            onMicrosoftSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Microsoft,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route)  },
                )
            },
            onTwitterSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Twitter,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route) },
                )
            },
            onYahooSignIn = {
                signUpViewModel.onOathLoginSelected(
                    oath = OathLogin.Yahoo,
                    activity = activity,
                    navigateToHome = { navController.navigate(Routes.HomeScreen.route)},
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
fun SignUpByMail(
    onSignUpEmail: (user: String, password: String) -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
){

    val context = LocalContext.current

    var user by rememberSaveable {
        mutableStateOf("")
    }

    var isError by rememberSaveable {
        mutableStateOf(false)
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisibility by rememberSaveable {
        mutableStateOf(false)
    }

    TextField(
        label = { Text(text="Usuario") },
        value = user,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        onValueChange = {
            if(signUpViewModel.isEmail(it)){
                isError = false
            } else {
                isError = true
            }
            user = it
        },
        singleLine = true,
        maxLines = 1
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        onValueChange = {password = it},
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            val image = if(passwordVisibility){
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = "Show password")
            }
        },
        visualTransformation = if(passwordVisibility){
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                if(isError){
                    Toast.makeText(context, "El usuario introducido debe ser un email",Toast.LENGTH_LONG).show()
                } else {
                    onSignUpEmail(user, password)
                }
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