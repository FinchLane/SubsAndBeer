package com.example.barbershop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun MessagesLogin(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp, top = 36.dp)){
        Image(
            painter = painterResource(R.drawable.message_background_small),
            contentDescription = null
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp)) {
            Text(
                text = "CSC Company",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Код:",
                    color = Color.White,
                    modifier = Modifier.padding(end = 4.dp)
                )
                SlotMachine(
                    emojis = listOf(1,2,3,4,5,6,7,8,9),
                    spinDuration = 2000L,
                    stopDelay = 5000L,
                    stopTime = 10000L
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMessageLogin() {
    BarbershopTheme {
        MessagesLogin()
    }
}