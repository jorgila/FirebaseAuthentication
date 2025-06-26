package com.estholon.firebaseauthentication.ui.screens.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes


@Composable
fun RecoverScreen(
    recoverViewModel: RecoverViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val context = LocalContext.current
    val uiState by recoverViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SignInLink(onCreateAccount = { navController.navigate(Routes.SignInScreen.route) })
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Authentication"
        )
        Spacer(modifier = Modifier.height(30.dp))
        RecoverPassword(
            onRecoverPassword = {user ->
                recoverViewModel.resetPassword(
                    email = user,
                    navigateToSignIn = { navController.navigate(Routes.SignInScreen.route) },
                    communicateError = { Toast.makeText(context,uiState.error.toString(),Toast.LENGTH_LONG).show() },
                )
            }
        )
        Spacer(modifier = Modifier.height(350.dp))
    }
}

@Composable
fun RecoverPassword(
    onRecoverPassword: (email: String) -> Unit,
    recoverViewModel: RecoverViewModel = hiltViewModel()
){

    val uiState = recoverViewModel.uiState.collectAsState()

    var user by rememberSaveable {
        mutableStateOf("")
    }

    TextField(
        label = { Text(text="Usuario") },
        value = user,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        onValueChange = {
            recoverViewModel.isEmailValid(it)
            user = it
        },
        singleLine = true,
        maxLines = 1,
        isError = !uiState.value.isEmailValid
    )

    if(!uiState.value.isEmailValid) {
        Text("Introduce un email válido", color = Color.Red)
    }
    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                onRecoverPassword(user)
            },
            enabled = (uiState.value.isEmailValid),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text(text = "Recuperar Cuenta".uppercase())
        }
    }

}