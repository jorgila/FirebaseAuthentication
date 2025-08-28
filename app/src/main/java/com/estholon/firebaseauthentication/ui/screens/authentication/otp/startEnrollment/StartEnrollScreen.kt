package com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estholon.firebaseauthentication.ui.screens.authentication.otp.startEnrollment.models.StartEnrollState

@Composable
fun StartEnrollScreen(
    state: StartEnrollState = StartEnrollState(),
    sendOTP: (String) -> Unit = {}
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        var phone by rememberSaveable { mutableStateOf("") }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            value = phone,
            onValueChange = { phone = it },
            label = { Text(text = "Número de teléfono") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (phone.isNotEmpty()) {
                    sendOTP(phone)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Enviar OTP")
        }
    }

     if (state.loading) {
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
     }
}

@Preview
@Composable
fun StartEnrollScreenPreview() {
    StartEnrollScreen()
}