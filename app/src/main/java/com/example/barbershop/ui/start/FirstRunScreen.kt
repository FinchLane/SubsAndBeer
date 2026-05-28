package com.example.barbershop.ui.start

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun FirstRunScreen(modifier: Modifier = Modifier) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ){
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Добро пожаловать!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Мы можем просканировать ваше устройство и показать приложения с подписками. Вы сами отметите, какие из них активны.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton(
                        onClick = {}
                    ) {
                        Text(
                            text = "Пропустить",
                            color = Color.LightGray
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {}
                    ) {
                        Text(
                            text = "Разрешить"
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FirstRunScreenPreview() {
    BarbershopTheme {
        FirstRunScreen()
    }
}