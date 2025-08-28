package com.estholon.firebaseauthentication.ui.screens.authentication.otp

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estholon.firebaseauthentication.ui.core.components.otp.OtpCodeInput
import com.estholon.firebaseauthentication.ui.screens.authentication.signUp.SignUpUiState
import com.facebook.CallbackManager
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StartEnrollScreen(
    state: StateFlow<SignUpUiState>? = null,
    sendOTP: (String) -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        var phone by rememberSaveable { mutableStateOf("") }
        var code by rememberSaveable { mutableStateOf("") }

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(text = "Número de teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OtpCodeInput(
            value = code,
            onValueChange = { code = it },
            onFilled = { /* Submit code */ },
            isError = false,
            cellSize = 52.dp,
            cellSpacing = 10.dp,
            textStyle = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                if (code.isNotEmpty()) {
                    sendOTP(code)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Enviar OTP")
        }
    }

    /* if (state.isLoading) {
         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .semantics {
                     contentDescription = "Creando cuenta, por favor espere"
                     liveRegion = LiveRegionMode.Polite
                 }) {
             CircularProgressIndicator(
                 modifier = Modifier
                     .size(100.dp)
                     .align(Alignment.Center)
                     .semantics {
                         contentDescription = "Cargando"
                     })
         }
     }*/
}

@Preview
@Composable
fun StartEnrollScreenPreview() {
    StartEnrollScreen()
}