package com.estholon.firebaseauthentication.ui.screens.authentication.signIn

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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.core.components.authentication.OtherMethods
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.signIn.models.SignInState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun SignInScreen(
    state: SignInState = SignInState(),
    onIntent: (SignInEvent) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToRecover: () -> Unit,
    navigateToHome: () -> Unit,
) {

    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val callbackManager = CallbackManager.Factory.create()

    // LAUNCHED EFFECTS

// AUTOMATIC LAUNCH FOR SIGN IN WITH GOOGLE
//    LaunchedEffect(Unit) {
//        signInViewModel.signInGoogleCredentialManager(
//            activity = activity,
//            navigateToHome = { navController.navigate(HomeScreen.route) }
//        )
//    }

    LaunchedEffect(state.shouldNavigateToHome) {
        if(state.shouldNavigateToHome) {
            navigateToHome()
        }
    }

    LaunchedEffect(state.shouldShowEmailError) {
        if(state.shouldShowEmailError) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SignUpLink(onCreateAccountPressed = { navigateToSignUp() })
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication"
        )
        Spacer(modifier = Modifier.height(30.dp))
        SignInByMail(
            onSignInEmail = { user, password ->
                SignInEvent.SignInEmail(user,password)
            },
            onForgotPassword = { navigateToRecover() },
            onEmailChange = { email ->
                onIntent(SignInEvent.CheckIfEmailIsValid(email))
            },
            onPasswordChange = { password ->
                onIntent(SignInEvent.CheckIfPasswordIsValid(password))
            }
        )
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
                    onIntent(SignInEvent.SignInFacebook(result.accessToken))
                }
            })

        // Facebook End

        OtherMethods(
            onPhoneSignIn = {  }, //TODO
            onAnonymously = {
                onIntent(SignInEvent.SignInAnonymously)
            },
            onGoogleSignIn = {
                onIntent(SignInEvent.SignInGoogle(activity))
            },
            onFacebookSignIn = {
                LoginManager.getInstance()
                    .logInWithReadPermissions(context as ActivityResultRegistryOwner, callbackManager, listOf("email", "public_profile"))
            },
            onGitHubSignIn = {
                onIntent(SignInEvent.OnOathLoginSelected(OathLogin.GitHub,activity))
            },
            onMicrosoftSignIn = {
                onIntent(SignInEvent.OnOathLoginSelected(OathLogin.Microsoft,activity))
            },
            onTwitterSignIn = {
                onIntent(SignInEvent.OnOathLoginSelected(OathLogin.Twitter,activity))
            },
            onYahooSignIn = {
                onIntent(SignInEvent.OnOathLoginSelected(OathLogin.Yahoo,activity))
            }
        )
    }

    if(state.isLoading){
        Box(modifier = Modifier.fillMaxSize().semantics {
            contentDescription = "Iniciando sesión, por favor espere"
            liveRegion = LiveRegionMode.Polite
        }){
            CircularProgressIndicator(modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .semantics {
                    contentDescription = "Cargando"
                })
        }
    }

}

@Composable
fun SignUpLink(onCreateAccountPressed: () -> Unit){
    Box(modifier = Modifier.width(300.dp), contentAlignment = Alignment.TopEnd){

        TextButton(
            onClick = {
                onCreateAccountPressed()
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
    state: SignInState = SignInState(),
    onSignInEmail: (user: String, password: String) -> Unit,
    onForgotPassword: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
){

    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current

    var user by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisibility by rememberSaveable {
        mutableStateOf(false)
    }

    TextField(
        label = { Text(text="Usuario")},
        value = user,
        isError = !state.isEmailValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down)}
        ),
        onValueChange = {
            onEmailChange(it)
            user = it
        },
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.semantics {
            contentDescription = "Campo de correo electrónico"
            if (!state.isEmailValid && state.emailError != null) {
                stateDescription = state.emailError!!
            }
        },
        supportingText = if (!state.isEmailValid && state.emailError != null) {
            { Text(state.emailError!!, color = MaterialTheme.colorScheme.error) }
        } else null
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        onValueChange = {
            onPasswordChange(it)
            password = it
        },
        isError = !state.isPasswordValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onSignInEmail(user, password)
            }
        ),
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
            if (!state.isPasswordValid && state.passwordError != null) {
                stateDescription = state.passwordError!!
            }
        },
        supportingText = if (!state.isPasswordValid && state.passwordError != null) {
            { Text(state.passwordError!!, color = MaterialTheme.colorScheme.error) }
        } else null
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onSignInEmail(user, password)
            },
            enabled = (state.isEmailValid && state.isPasswordValid),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
                .semantics {
                    contentDescription = "Iniciar sesión con correo electrónico"
                    if(!state.isEmailValid && !state.isPasswordValid){
                        disabled()
                    }
                }
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


