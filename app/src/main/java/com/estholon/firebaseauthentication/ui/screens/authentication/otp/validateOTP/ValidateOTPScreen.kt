package com.estholon.firebaseauthentication.ui.screens.authentication.otp.validateOTP

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.estholon.firebaseauthentication.ui.core.components.otp.OtpCodeInput

@Composable
fun ValidateOTPScreen(modifier: Modifier = Modifier) {
    var code by rememberSaveable { mutableStateOf("") }

    OtpCodeInput(
        value = code,
        onValueChange = { code = it },
        onFilled = { /* Submit code */ },
        isError = false,
        cellSize = 52.dp,
        cellSpacing = 10.dp,
        textStyle = MaterialTheme.typography.headlineSmall
    )
}