package com.example.barbershop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.barbershop.R
import com.example.barbershop.ui.ducks.GIFImage
import kotlinx.coroutines.delay

@Composable
fun Load(modifier: Modifier = Modifier) {

    val loadingMessages = listOf(
        stringResource(R.string.loading_messages_1),
        stringResource(R.string.loading_messages_2),
        stringResource(R.string.loading_messages_3),
        stringResource(R.string.loading_messages_4),
        stringResource(R.string.loading_messages_5),
        stringResource(R.string.loading_messages_6),
        stringResource(R.string.loading_messages_7),
        stringResource(R.string.loading_messages_8),
        stringResource(R.string.loading_messages_9),
        stringResource(R.string.loading_messages_10)
    )

    var currentMessage by remember {
        mutableStateOf(loadingMessages.random())
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(20000L)
            currentMessage = loadingMessages.random()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            GIFImage(data = R.drawable.duck_load_bg2_, modifier = Modifier.size(88.dp))
            //Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = currentMessage,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}