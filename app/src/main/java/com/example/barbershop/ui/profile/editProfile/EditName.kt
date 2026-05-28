package com.example.barbershop.ui.profile.editProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.Constants.mainPadding
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditName(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "editProfile" -> {
                navController.navigate("editProfile") {
                    popUpTo("editName") { inclusive = true }
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

    var firstName by remember { mutableStateOf(viewModel.firstNameUser) }
    var lastName by remember { mutableStateOf(viewModel.lastNameUser) }
    var middleName by remember { mutableStateOf(viewModel.middleNameUser) }

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()})},
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->

        Box (modifier = modifier
            .fillMaxSize()
            .padding(start = mainPadding, end = mainPadding))
        {
            Column(
                modifier = modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Полное имя",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = lastName,
                    onValueChange = {value ->
                        lastName = value
                    },
                    label = { Text("Фамилия")},
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = firstName,
                    onValueChange = { value ->
                        firstName = value
                    },
                    label = { Text("Имя")},
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = middleName,
                    onValueChange = {value ->
                        middleName = value
                    },
                    label = { Text("Отчество") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.editName(
                            firstName = firstName,
                            lastName = lastName,
                            middleName = middleName
                        )
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Сохранить",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun EditNamePreview() {
//    BarbershopTheme {
//        EditName(rememberNavController())
//    }
//}