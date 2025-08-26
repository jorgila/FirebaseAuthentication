package com.estholon.firebaseauthentication.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes


@Composable
fun RecoverScreen(
    recoverViewModel: RecoverViewModel,
    navigateToSignIn: () -> Unit
) {

    val context = LocalContext.current
    val uiState by recoverViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SignInLink(onCreateAccount = { navigateToSignIn() })
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication"
        )
        Spacer(modifier = Modifier.height(30.dp))
        RecoverPassword(
            onRecoverPassword = {user ->
                recoverViewModel.resetPassword(
                    email = user,
                    navigateToSignIn = { navigateToSignIn() },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() },
                )
            }
        )
        Spacer(modifier = Modifier.height(350.dp))
    }

    if(uiState.isLoading){
        Box(modifier = Modifier.fillMaxSize().semantics {
            contentDescription = "Enviando correo, por favor espere"
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
fun RecoverPassword(
    onRecoverPassword: (email: String) -> Unit,
    recoverViewModel: RecoverViewModel = hiltViewModel()
){

    val uiState by recoverViewModel.uiState.collectAsState()

    val hapticFeedback = LocalHapticFeedback.current

    var user by rememberSaveable {
        mutableStateOf("")
    }

    TextField(
        label = { Text(text="Usuario") },
        value = user,
        onValueChange = {
            recoverViewModel.isEmailValid(it)
            user = it
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onRecoverPassword(user)
            }
        ),
        singleLine = true,
        maxLines = 1,
        isError = !uiState.isEmailValid,
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

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onRecoverPassword(user)
            },
            enabled = (uiState.isEmailValid),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
                .semantics {
                    contentDescription = "Recuperar contraseña con correo electrónico"
                    if(!uiState.isEmailValid){
                        disabled()
                    }
                }
        ) {
            Text(text = "Recuperar Cuenta".uppercase())
        }
    }

}