package com.example.barbershop.ui.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.Keyboard
import com.example.barbershop.ui.components.Load
import com.example.barbershop.ui.components.customComponent.PhoneNumberMaskTransformation
import com.example.barbershop.ui.components.customComponent.TextInputContainer
import com.example.barbershop.ui.components.notification.ErrorNotification
import com.example.barbershop.ui.ducks.GIFImage
import com.example.barbershop.viewmodel.authorization.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    ipAddress: String,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var enteredPhoneNumber by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "confirmation?phoneNumber=${viewModel.phoneNumber}" -> {
                navController.navigate("confirmation?phoneNumber=${viewModel.phoneNumber}")
                viewModel.clearNavigationEvent()
            }
            "home" -> {
                navController.navigate("home")
                viewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        if (viewModel.errorMessage.isNotEmpty()){
            isErrorVisible = true
        }
    }

    Box {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            GIFImage(data = R.drawable.welcome_duck)
            Spacer(modifier = Modifier.weight(1f))
            TextInputContainer(
                title = stringResource(R.string.authorization),
                title2 = stringResource(R.string.enter_phone_number),
                textFieldTitle = stringResource(R.string.phone_number),
                value = enteredPhoneNumber,
                onValueChange = { newValue ->
                    enteredPhoneNumber = newValue.filter { it.isDigit() }.take(10)
                },
                PhoneNumberMaskTransformation(),
                leadingIcon = {
                    Text(text = "+7")
                }
            )

            TextButton(
                onClick = {
                    viewModel.setUnauthorizedAccess()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Продолжить без аккаунта",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.onPhoneNumberChange(enteredPhoneNumber)
                        viewModel.sendSms(ipAddress)
                    },
                    contentPadding = PaddingValues(0.dp),
                    enabled = enteredPhoneNumber.length >= 10,
                    modifier = Modifier
                        .size(72.dp)
                        .padding(bottom = 8.dp, end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.right_arrows),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Keyboard({
                if (enteredPhoneNumber.length < 10) {
                    enteredPhoneNumber = enteredPhoneNumber.plus(it)
                }
            }, {
                if (enteredPhoneNumber.isNotEmpty()) {
                    enteredPhoneNumber = enteredPhoneNumber.dropLast(1)
                }
            },
                modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
            )
        }

        ErrorNotification(
            text = viewModel.errorMessage,
            isVisible = isErrorVisible,
            onDismiss = {
                isErrorVisible = false
                viewModel.clearErrorMessage()
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (viewModel.isLoading){
            Load()
        }
    }
}

//@Preview("LightTheme", showBackground = true)
//@Preview("DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true,
//    backgroundColor = 0xFF2C2A2A
//)
//@Composable
//private fun LoginPreview() {
//    BarbershopTheme {
//        LoginScreen( ipAddress = "-1")
//    }
//}
