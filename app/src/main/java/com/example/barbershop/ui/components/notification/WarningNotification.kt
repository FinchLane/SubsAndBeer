package com.example.barbershop.ui.components.notification

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme
import kotlinx.coroutines.delay

@Composable
fun WarningNotification(text: String, isVisible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = Modifier.padding(8.dp)
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFd5782f),
                    shape = RoundedCornerShape(12.dp)
                )
        )
        {
            Icon(
                painter = painterResource(R.drawable.exclamation),
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
    LaunchedEffect(isVisible) {
        if (isVisible){
            delay(3000L)
            onDismiss()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WarningNotificationPreview() {
    BarbershopTheme {
        WarningNotification("Сообщение", true, {})
    }
}