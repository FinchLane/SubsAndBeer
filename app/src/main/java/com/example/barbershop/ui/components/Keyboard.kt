package com.example.barbershop.ui.components

import android.content.Context
import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun Keyboard(onNumberClick : (String) -> Unit, onDeleteClick : () -> Unit, modifier: Modifier = Modifier) {
    val view = LocalView.current

    val numbers = listOf(
        "1" to "", "2" to "ABC", "3" to "DEF",
        "4" to "GHI", "5" to "JKL", "6" to "MNO",
        "7" to "PQRS", "8" to "TUV", "9" to "WXYZ"
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (row in numbers.chunked(3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for ((number, letters) in row) {
                    Button(
                        onClick = {
                            onNumberClick(number)
                            view.vibrate()
                        },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(Color.DarkGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = number,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier
                                .size(8.dp)
                                .weight(1f))
                            Text(
                                text = letters,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier
                .weight(1f)
                .padding(2.dp))

            Button(
                onClick = {
                    onNumberClick("0")
                    view.vibrate()
                },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier
                    .size(8.dp)
                    .weight(1f))
                Text(
                    text = "+",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    onDeleteClick()
                    view.vibrate()
                },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview("LightTheme", showBackground = true)
@Preview("DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun KeyboardPreview() {
    BarbershopTheme {
        Keyboard({}, {})
    }
}

/** Настройка вибрации при нажатии на кастомную клаву (т.к. обычный способ не сработал) */

fun View.vibrate() = reallyPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

private fun View.reallyPerformHapticFeedback(feedbackConstant: Int){
    if (context.isTouchExplorationEnabled()) {
        return
    }

    isHapticFeedbackEnabled = true

    performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)

}

private fun Context.isTouchExplorationEnabled(): Boolean {
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}