package com.estholon.firebaseauthentication.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.result.ActivityResultRegistryOwner
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
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
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToSignIn: () -> Unit
){
    val activity = LocalActivity.current!!
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()

    lateinit var callbackManager: CallbackManager

    // Facebook

    callbackManager = CallbackManager.Factory.create()

    LoginManager.getInstance()
        .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Toast.makeText(context,"¿Probamos otra red social?",Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context,"Ha ocurrido un error: ${error.message}",Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                homeViewModel.onLinkFacebook(
                    result.accessToken,
                    communicateSuccess = { Toast.makeText(context,"Linked Successfully",Toast.LENGTH_LONG).show() },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show()  }
                )
            }

        })

    // Facebook End


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(30.dp))
            LinkWithMail(
                onLinkWithEmail = { user, password ->
                    homeViewModel.onLinkEmail(
                        email = user,
                        password = password,
                        communicateSuccess = { Toast.makeText(context,"Cuenta vinculada",Toast.LENGTH_LONG).show()},
                        communicateError = { Toast.makeText(context,uiState.error ?: "Cuenta no vinculada",Toast.LENGTH_LONG).show()},
                    )
                }
            )
            Spacer(modifier = Modifier.height(30.dp))

            LinkWithOtherMethods(
                onGoogleLink = { homeViewModel.onLinkGoogle(
                    activity = activity,
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onFacebookLink = {
                    LoginManager.getInstance()
                        .logInWithReadPermissions(context as ActivityResultRegistryOwner, callbackManager, listOf("email", "public_profile"))
                },
                onGitHubLink = { homeViewModel.onLinkGitHub(
                    activity = activity,
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,uiState.error,Toast.LENGTH_LONG).show()},
                ) },
                onMicrosoftLink = { homeViewModel.onLinkMicrosoft(
                    activity = activity,
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,uiState.error,Toast.LENGTH_LONG).show()},
                ) },
                onTwitterLink = { homeViewModel.onLinkTwitter(
                    activity = activity,
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,uiState.error,Toast.LENGTH_LONG).show()},
                ) },
                onYahooLink = { homeViewModel.onLinkYahoo(
                    activity = activity,
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,uiState.error,Toast.LENGTH_LONG).show()},
                ) }
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                homeViewModel.logout {
                    navigateToSignIn()
                }
            }) {
                Text(text = "LOGOUT")
            }
        }

        if(uiState.isLoading){
            Box(modifier = Modifier.fillMaxSize().semantics {
                contentDescription = "Cargando, por favor espere"
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

}

@Composable
fun LinkWithMail(
    onLinkWithEmail: (user: String, password: String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
){

    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current

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

    Text(text = "Vincular con una cuenta de correo")

    Spacer(modifier = Modifier.height(30.dp))

    TextField(
        label = { Text(text="Usuario")},
        value = user,
        isError = !uiState.isEmailValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down)}
        ),
        onValueChange = {
            homeViewModel.isEmailValid(it)
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
        isError = !uiState.isPasswordValid,
        value = password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onLinkWithEmail(user,password)
            }
        ),
        onValueChange = {
            homeViewModel.isPasswordValid(it)
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
                Icon(imageVector = image, contentDescription = "Show password")
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
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onLinkWithEmail(user, password)
            },
            enabled = (user != null && password.length >= 6),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text(text = "Vincular cuenta".uppercase())
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun LinkWithOtherMethods(
    onGoogleLink : () -> Unit,
    onFacebookLink : () -> Unit,
    onGitHubLink : () -> Unit,
    onMicrosoftLink : () -> Unit,
    onTwitterLink : () -> Unit,
    onYahooLink : () -> Unit
){
    Text(text = "Vincular con otros métodos")
    Spacer(modifier = Modifier.height(30.dp))
    Column {
        Row {
            FloatingActionButton(
                onClick = {  }
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
                onClick = { onGoogleLink() }
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
                onClick = { onFacebookLink() }
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
                onClick = { onGitHubLink() }
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
                onClick = { onMicrosoftLink() }
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
                onClick = { onTwitterLink() }
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
                onClick = { onYahooLink() }
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
        }
    }
}
