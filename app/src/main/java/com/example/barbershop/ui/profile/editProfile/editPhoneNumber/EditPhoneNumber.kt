package com.example.barbershop.ui.profile.editProfile.editPhoneNumber

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.barbershop.Constants.mainPadding
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.profile.ProfileViewModel

@Composable
fun EditPhoneNumber(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}) },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(modifier = modifier.padding(start = mainPadding, end = mainPadding)) {
            Column(modifier = Modifier.padding(innerPadding)) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Главное - безопасность",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Мы отправим код подтверждения на ваш текущий номер ${viewModel.phoneNumberUser}",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {},
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Получить код"
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = {},
                    ) {
                        Text("Нет доступа к телефону?")
                    }
                }
            }
        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun EditPhoneNumberPreview() {
//    BarbershopTheme {
//        EditPhoneNumber(rememberNavController())
//    }
//}