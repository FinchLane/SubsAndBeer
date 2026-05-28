package com.example.barbershop.ui.profile.editProfile

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun RelatedAccountsScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                AccountItem(R.drawable.free_icon_telegram_2111646, "Telegram", true) { }
                AccountItem(R.drawable.free_icon_discord_5968756, "Discord", false) { }
                AccountItem(R.drawable.free_icon_github_2111432, "Github", false) { }
                AccountItem(R.drawable.free_icon_search_281764, "Google", false) { }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RelatedAccountsScreenPreview() {
    BarbershopTheme {
        RelatedAccountsScreen(navController = rememberNavController())
    }
}

@Composable
fun AccountItem(image: Int, name: String, linked: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))
            if (linked){
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(Color(0xFF412a2b))
                ) {
                    Text(
                        text = "Отключить"
                    )
                }
            }
            else{
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(Color.DarkGray)
                ) {
                    Text(
                        text = "Подключить"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AccountItemPreview() {
    BarbershopTheme {
       Column {
           AccountItem(R.drawable.free_icon_telegram_2111646, "Telegram", false) { }
           AccountItem(R.drawable.free_icon_telegram_2111646, "Telegram", true) { }
       }
    }
}
