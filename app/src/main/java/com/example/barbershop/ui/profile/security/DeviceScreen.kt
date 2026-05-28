package com.example.barbershop.ui.profile.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.Constants.mainPadding
import com.example.barbershop.data.network.response.UserSession
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.security.SecurityViewModel
import com.google.gson.Gson

@Composable
fun DeviceScreen(
    navController: NavController,
    sessionJson: String,
    viewModel: SecurityViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "authDevices" -> {
                navController.navigate("authDevices")
                viewModel.clearNavigationEvent()
            }
            "login" -> {
                navController.navigate("login")
                viewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }

    val session = Gson().fromJson(sessionJson, UserSession::class.java)
    Scaffold(
        topBar = {
            AppBarBack(nav = {navController.popBackStack()})
        }
    ) {innerPadding ->
        Box(
            modifier = modifier.padding(start = mainPadding, end = mainPadding)
        ){
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = session.deviceName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                DeviceInfo(
                    deviceData = listOf(
                        "Тип" to session.deviceName,
                        "Версия приложения" to session.appVersion,
                        "Время" to session.startedAt,
                        "IP-адрес" to session.ipAddress
                    ) as List<Pair<String, String?>>
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.deleteSession(session.id) },
                    colors = ButtonDefaults.buttonColors(Color(0xFF412a2b)),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Выйти",
                        color = Color(0xFFff5c52)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DeviceInfo(deviceData: List<Pair<String, String?>>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)){
            deviceData.forEachIndexed { index, (title, description) ->
                if (index > 0) HorizontalDivider()
                DataRow(title, description.toString())
            }
        }
    }
}

@Composable
fun DataRow(title: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}