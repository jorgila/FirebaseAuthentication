package com.estholon.firebaseauthentication.ui.screens.home

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.estholon.firebaseauthentication.R
import com.estholon.firebaseauthentication.ui.navigation.Routes.*

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
){

    val context = LocalContext.current

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
                    homeViewModel.onLinkWithEmail(
                        user = user,
                        password = password,
                        communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                        communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                    )
                }
            )
            Spacer(modifier = Modifier.height(30.dp))

            LinkWithOtherMethods(
                onGoogleLink = { homeViewModel.onLinkWithGoogle(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onFacebookLink = { homeViewModel.onLinkWithFacebook(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onGitHubLink = { homeViewModel.onLinkWithGitHub(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onMicrosoftLink = { homeViewModel.onLinkWithMicrosoft(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onTwitterLink = { homeViewModel.onLinkWithTwitter(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) },
                onYahooLink = { homeViewModel.onLinkWithYahoo(
                    communicateSuccess = { Toast.makeText(context,"Account linked",Toast.LENGTH_LONG).show()},
                    communicateError = { Toast.makeText(context,"Account not linked",Toast.LENGTH_LONG).show()},
                ) }
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                homeViewModel.logout {
                    navController.navigate(SignInScreen.route)
                }
            }) {
                Text(text = "LOGOUT")
            }
        }

        if(homeViewModel.isLoading){
            Box(modifier = Modifier.fillMaxSize()){
                CircularProgressIndicator(modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center))
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
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        onValueChange = {
            if(homeViewModel.isEmail(it)){
                isError = false
            } else {
                isError = true
            }
            user = it
        },
        singleLine = true,
        maxLines = 1
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        label = { Text(text = "Contraseña") },
        value = password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        onValueChange = {password = it},
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
        }
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = {
                if(isError){
                    Toast.makeText(context, "El usuario introducido debe ser un email",Toast.LENGTH_LONG).show()
                } else {
                    onLinkWithEmail(user, password)
                }
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
