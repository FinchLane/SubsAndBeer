package com.example.barbershop.ui.profile.editProfile.editEmail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.Constants.mainPadding
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmail(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "editProfile" -> {
                navController.navigate("editProfile") {
                    popUpTo("editEmail") { inclusive = true }
                    popUpTo("editProfile") { inclusive = true }
                }
                viewModel.clearNavigationEvent()
            }
            "login" -> {
                navController.navigate("login") {
                    popUpTo(0)
                }
                viewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }


    var email by remember { mutableStateOf(viewModel.emailUser) }

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize().padding(start = mainPadding, end = mainPadding)
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Укажите почту",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Она нужна, чтобы защитить ваш аккаунт",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { value ->
                        email = value
                    },
                    label = { Text("Почта")},
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                // нужно будет добавит подтверждение, но пока так
                Button(
                    onClick = {
                        viewModel.editEmail(email)
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Получить код")
                }
            }
        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun EditEmailPreview() {
//    BarbershopTheme {
//        EditEmail(rememberNavController())
//    }
//}