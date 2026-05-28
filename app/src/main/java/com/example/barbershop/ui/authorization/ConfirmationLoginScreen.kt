package com.example.barbershop.ui.authorization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.CodeInput
import com.example.barbershop.ui.components.Keyboard
import com.example.barbershop.ui.components.Load
import com.example.barbershop.ui.components.SlotMachine
import com.example.barbershop.ui.components.notification.ErrorNotification
import com.example.barbershop.utils.readCallLog
import com.example.barbershop.viewmodel.authorization.AuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConfirmationLoginScreen(
    viewModel: AuthViewModel,
    ipAddress: String,
    phoneNumber: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }
    var timer by remember { mutableIntStateOf(180) }
    var isButtonEnabled by remember { mutableStateOf(false)}
    var isErrorVisible by remember { mutableStateOf(false) }
    var codesSent by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    val callLogPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.READ_CALL_LOG
    )

    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "home" -> {
                navController.navigate("home")
                viewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }

    LaunchedEffect(key1 = timer) {
        if (timer > 0){
            delay(1000L)
            timer--
        }
        else{
            isButtonEnabled = codesSent < 2
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        if (viewModel.errorMessage.isNotEmpty()){
            isErrorVisible = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onPhoneNumberChange(phoneNumber)
    }

    LaunchedEffect(Unit) {
        if (callLogPermissionState.status != PermissionStatus.Granted) {
            callLogPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+7 (XXX) XXX - ",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                ) {
                    SlotMachine(
                        emojis = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
                        spinDuration = 2000L,
                        stopDelay = 5000L,
                        stopTime = 10000L
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.enter_confirmation_code),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 28.dp)
                )
                Text(
                    text = stringResource(R.string.send_call_v2, viewModel.phoneNumber),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                CodeInput(
                    code = code,
                    onCodeChange = { newCode ->
                        code = newCode.filter { it.isDigit() }.take(4)
                    }
                )
            }

            if (codesSent < 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (timer > 0) {
                            stringResource(
                                R.string.send_code_timer,
                                timer / 60,
                                (timer % 60).toString().padStart(2, '0')
                            )
                        } else {
                            stringResource(R.string.send_code)
                        },
                        color = if (!isButtonEnabled) {
                            Color.Gray
                        } else {
                            Color.White
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable(
                                enabled = isButtonEnabled && codesSent < 2,
                                onClick = {
                                    codesSent++
                                    if (codesSent <= 2) {
                                        timer = 300
                                        isButtonEnabled = false
                                        viewModel.sendSms(ipAddress)
                                    }
                                }
                            ),
                    )
                }
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
                        viewModel.onCodeChange(code)
                        viewModel.verifyCode()
                    },
                    contentPadding = PaddingValues(0.dp),
                    enabled = code.length >= 4,
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

            Keyboard(
                onNumberClick = {
                    if (code.length < 4) {
                        code = code.plus(it)
                    }
                },
                onDeleteClick = {
                    if (code.isNotEmpty()) {
                        code = code.dropLast(1)
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

    LaunchedEffect(callLogPermissionState.status) {
        if (callLogPermissionState.status == PermissionStatus.Granted) {
            val lastCode = readCallLog(context)
            lastCode?.let {
                code = it
            }
        }
    }
}

//@Preview("DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun ConfirmationPreview() {
//    BarbershopTheme {
//        ConfirmationLogin(ipAddress = "-1")
//    }
//}