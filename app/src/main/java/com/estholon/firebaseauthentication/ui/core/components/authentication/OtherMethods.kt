package com.estholon.firebaseauthentication.ui.core.components.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.estholon.firebaseauthentication.R

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

    val hapticFeedback = LocalHapticFeedback.current

    Text(text = "Otros métodos")
    Spacer(modifier = Modifier.height(30.dp))
    Column {
        Row {
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPhoneSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con el teléfono"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGoogleSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con Google"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFacebookSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con Facebook"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fb),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)

                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGitHubSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con GitHub"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onMicrosoftSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con Microsoft"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_microsoft),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTwitterSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con Twitter"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_twitter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onYahooSignIn()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión o crear cuenta con Yahoo"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_yahoo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAnonymously()
                },
                modifier = Modifier
                    .size(56.dp)
                    .semantics {
                        contentDescription = "Iniciar sesión con Anonymously"
                        role = Role.Button
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_anonymously),
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(8.dp)
                )
            }

        }
    }
}
