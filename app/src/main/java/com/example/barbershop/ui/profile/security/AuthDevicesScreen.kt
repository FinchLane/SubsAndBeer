package com.example.barbershop.ui.profile.security

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.Constants.mainPadding
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.security.SecurityViewModel
import com.google.gson.Gson

@Composable
fun AuthDevicesScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.session()
    }

    Scaffold(
        topBar = {
            AppBarBack(nav = {navController.popBackStack()})
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(start = mainPadding, end = mainPadding)
        ){
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Aвторизованные сеансы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Сеансы и устройства, на которых вы уже заходили в профиль",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ТЕКУЩИЙ",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            viewModel.currentSession?.let {
                                Text(
                                    text = it.deviceName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                Text(
                                    text = "Версия приложения: ${it.appVersion}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = it.startedAt,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
                if (viewModel.otherSessions.isNotEmpty()){
                    Text(
                        text = "ОСТАЛЬНЫЕ",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.otherSessions) { session ->
                            DevicesElement(
                                device = session.deviceName,
                                version = session.appVersion,
                                time = session.startedAt,
                                modifier = Modifier.clickable {
                                    val sessionJson = Gson().toJson(session)
                                    navController.navigate("device/$sessionJson")
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {viewModel.deleteAllSession()},
                        colors = ButtonDefaults.buttonColors(Color(0xFF412a2b)),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Выйти из всех сеансов",
                            color = Color(0xFFff5c52)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DevicesElement(device: String, version: String, time: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                Text(
                    text = device,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Версия приложения: $version",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DevicesElementPreview() {
    BarbershopTheme {
        DevicesElement("Galaxy A5", "1.0.0", "25 дек. 2024 в 13:06")
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun AuthDevicesPreview() {
//    BarbershopTheme {
//        AuthDevices(rememberNavController())
//    }
//}