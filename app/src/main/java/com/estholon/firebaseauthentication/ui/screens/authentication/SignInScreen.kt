package com.estholon.firebaseauthentication.ui.screens.authentication

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes.*
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val uiState by signInViewModel.uiState.collectAsState()
    val callbackManager = CallbackManager.Factory.create()

// AUTOMATIC LAUNCH FOR SIGN IN WITH GOOGLE
//    LaunchedEffect(Unit) {
//        signInViewModel.signInGoogleCredentialManager(
//            activity = activity,
//            navigateToHome = { navController.navigate(HomeScreen.route) }
//        )
//    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SignUpLink(onCreateAccount = { navController.navigate(SignUpScreen.route) })
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication"
        )
        Spacer(modifier = Modifier.height(30.dp))
        SignInByMail(
            onSignInEmail = { user, password -> signInViewModel.signInEmail(
                email = user,
                password = password,
                navigateToHome = { navController.navigate(HomeScreen.route) },
                communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show()  }
            ) },
            onForgotPassword = { navController.navigate(RecoverScreen.route)})
        Spacer(modifier = Modifier.height(30.dp))

        // Facebook

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(context,"Probamos otra red social?",Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(context,"Ha ocurrido un error: ${error.message}",Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(result: LoginResult) {
                    signInViewModel.signInFacebook(
                        result.accessToken,
                        navigateToHome = { navController.navigate(HomeScreen.route) },
                        communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show()  }
                    )
                }

            })

        // Facebook End

        OtherMethods(
            onPhoneSignIn = {  }, //TODO
            onAnonymously = {
                signInViewModel.signInAnonymously(
                    navigateToHome = { navController.navigate(HomeScreen.route) },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            },
            onGoogleSignIn = {
                signInViewModel.signInGoogle(
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route)},
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            },
            onFacebookSignIn = {
                LoginManager.getInstance()
                    .logInWithReadPermissions(context as ActivityResultRegistryOwner, callbackManager, listOf("email", "public_profile"))
            },
            onGitHubSignIn = {

                signInViewModel.onOathLoginSelected(
                    oath = OathLogin.GitHub,
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route) },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            },
            onMicrosoftSignIn = {

                signInViewModel.onOathLoginSelected(
                    oath = OathLogin.Microsoft,
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route) },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            },
            onTwitterSignIn = {
                signInViewModel.onOathLoginSelected(
                    oath = OathLogin.Twitter,
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route) },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            },
            onYahooSignIn = {
                signInViewModel.onOathLoginSelected(
                    oath = OathLogin.Yahoo,
                    activity = activity,
                    navigateToHome = { navController.navigate(HomeScreen.route)},
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() }
                )
            }
        )
    }

    if(uiState.isLoading){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center))
        }
    }

}

@Composable
fun SignUpLink(onCreateAccount: () -> Unit){
    Box(modifier = Modifier.width(300.dp), contentAlignment = Alignment.TopEnd){

        TextButton(
            onClick = {
                onCreateAccount()
            }
        ) {
            Text(
                text = "Crear cuenta".uppercase()
            )
        }

    }
}
@Composable
fun SignInByMail(
    onSignInEmail: (user: String, password: String) -> Unit,
    onForgotPassword: () -> Unit,
    signInViewModel: SignInViewModel = hiltViewModel()
){

    val context = LocalContext.current

    var user by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisibility by rememberSaveable {
        mutableStateOf(false)
    }

    val uiState by signInViewModel.uiState.collectAsState()

    TextField(
        label = { Text(text="Usuario")},
        value = user,
        isError = !uiState.isEmailValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        onValueChange = {
            signInViewModel.isEmailValid(it)
            user = it
        },
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.semantics {
            contentDescription = "Campo de correo electrónico"
            if (!uiState.isEmailValid && uiState.emailError != null) {
                stateDescription = uiState.emailError!!
            }
        },
        supportingText = if (!uiState.isEmailValid && uiState.emailError != null) {
            { Text(uiState.emailError!!, color = MaterialTheme.colorScheme.error) }
        } else null
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        isError = !uiState.isPasswordValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        onValueChange = {
            signInViewModel.isPasswordValid(it)
            password = it
        },
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            val image = if(passwordVisibility){
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = "Mostrar contraseña")
            }
        },
        visualTransformation = if(passwordVisibility){
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        modifier = Modifier.semantics {
            contentDescription = "Campo de contraseña"
            if (!uiState.isPasswordValid && uiState.passwordError != null) {
                stateDescription = uiState.passwordError!!
            }
        },
        supportingText = if (!uiState.isPasswordValid && uiState.passwordError != null) {
            { Text(uiState.passwordError!!, color = MaterialTheme.colorScheme.error) }
        } else null
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                if(!uiState.isEmailValid){
                    Toast.makeText(context, uiState.error,Toast.LENGTH_LONG).show()
                } else {
                    onSignInEmail(user, password)
                }
            },
            enabled = (uiState.isEmailValid && uiState.isPasswordValid),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text(text = "Iniciar Sesión".uppercase())
        }
    }
    Spacer(modifier = Modifier.height(10.dp))

    TextButton(onClick = {
        onForgotPassword()
    }) {
        Text(text = "He olvidado la contraseña")
    }
}

@Composable
fun OtherMethods(
    onPhoneSignIn: () -> Unit,
    onAnonymously : () -> Unit,
    onGoogleSignIn : () -> Unit,
    onFacebookSignIn : () -> Unit,
    onGitHubSignIn : () -> Unit,
    onMicrosoftSignIn : () -> Unit,
    onTwitterSignIn : () -> Unit,
    onYahooSignIn : () -> Unit
){
    Text(text = "Otros métodos")
    Spacer(modifier = Modifier.height(30.dp))
    Column {
        Row {
            FloatingActionButton(
                onClick = { onPhoneSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Phone",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = { onGoogleSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = { onFacebookSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fb),
                    contentDescription = "Facebook",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = { onGitHubSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = "GitHub",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            FloatingActionButton(
                onClick = { onMicrosoftSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_microsoft),
                    contentDescription = "Microsoft",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = { onTwitterSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_twitter),
                    contentDescription = "Twitter",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = { onYahooSignIn() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_yahoo),
                    contentDescription = "Yahoo",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    onAnonymously()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_anonymously),
                    contentDescription = "Anonymously",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }

        }
    }
}

