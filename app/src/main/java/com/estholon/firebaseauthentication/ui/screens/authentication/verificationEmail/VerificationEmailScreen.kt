package com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models.VerificationEmailEvent
import com.estholon.firebaseauthentication.ui.screens.authentication.verificationEmail.models.VerificationEmailState


@Composable
fun VerificationEmailScreen(
    state: State<VerificationEmailState> = mutableStateOf(VerificationEmailState()),
    onIntent: (VerificationEmailEvent) -> Unit,
    navigateToStartEnroll: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if(state.value.isEmailVerified){
            Text("Email is verified. You will be redirected...")
            navigateToStartEnroll()
        } else {
            Text("Email is not verified.")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onIntent(VerificationEmailEvent.SendEmailVerification)}
            ) {
                Text("Send verification email")
            }
            Button(
                onClick = { onIntent(VerificationEmailEvent.CheckIfEmailIsVerified)}
            ) {
                Text("Check if email is verified")
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun VerificationEmailScreenPreview(){
    VerificationEmailScreen(
        state = mutableStateOf(VerificationEmailState(isEmailVerified = false)),
        onIntent = { },
        navigateToStartEnroll = { }
    )
}