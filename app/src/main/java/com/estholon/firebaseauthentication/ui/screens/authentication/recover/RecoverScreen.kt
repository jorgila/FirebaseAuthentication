package com.estholon.firebaseauthentication.ui.screens.authentication.recover

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
import androidx.compose.runtime.State
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.screens.authentication.recover.models.RecoverEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.recover.models.RecoverState
import com.estholon.firebaseauthentication.ui.screens.authentication.signUp.SignInLink


@Composable
fun RecoverScreen(
    state: State<RecoverState> = mutableStateOf(RecoverState()),
    onIntent: (RecoverEvent) -> Unit,
    navigateToSignIn: () -> Unit
) {

    // VARIABLES

    val context = LocalContext.current


    // LAUNCHED EFFECTS

    LaunchedEffect(state.value.shouldNavigateToSignIn) {
        if(state.value.shouldNavigateToSignIn) {
            navigateToSignIn()
        }
    }

    LaunchedEffect(state.value.shouldShowError) {
        if(state.value.shouldShowError) {
            Toast.makeText(context, state.value.error, Toast.LENGTH_LONG).show()
        }
    }

    // VIEW

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
            onIntent = { event ->
                onIntent(event)
            },
            state
        )
        Spacer(modifier = Modifier.height(350.dp))
    }

    if(state.value.isLoading){
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
    onIntent: (RecoverEvent) -> Unit,
    state: State<RecoverState> = mutableStateOf(RecoverState())
){

    val hapticFeedback = LocalHapticFeedback.current

    var user by rememberSaveable {
        mutableStateOf("")
    }

    TextField(
        label = { Text(text="Usuario") },
        value = user,
        onValueChange = {
            onIntent(RecoverEvent.CheckIfEmailIsValid(it))
            user = it
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onIntent(RecoverEvent.ResetPassword(user))
            }
        ),
        singleLine = true,
        maxLines = 1,
        isError = !state.value.isEmailValid,
        modifier = Modifier.semantics {
            contentDescription = "Campo de correo electrónico"
            if (!state.value.isEmailValid && state.value.emailError != null) {
                stateDescription = state.value.emailError!!
            }
        },
        supportingText = if (!state.value.isEmailValid && state.value.emailError != null) {
            { Text(state.value.emailError!!, color = MaterialTheme.colorScheme.error) }
        } else null
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onIntent(RecoverEvent.ResetPassword(user))
            },
            enabled = (state.value.isEmailValid),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
                .semantics {
                    contentDescription = "Recuperar contraseña con correo electrónico"
                    if(!state.value.isEmailValid){
                        disabled()
                    }
                }
        ) {
            Text(text = "Recuperar Cuenta".uppercase())
        }
    }

}