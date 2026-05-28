package com.example.barbershop.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.barbershop.ui.theme.BarbershopTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    confettiCount: Int = 100,
    durationMillis: Int = 3000,
    startPosition: Offset = Offset(0f, 0f)

) {
    val confettiStateList = remember { List(confettiCount) { ConfettiState(startPosition) } }
    val infiniteTransition = rememberInfiniteTransition()

    confettiStateList.forEach { confetti ->
        confetti.offsetX = infiniteTransition.animateFloat(
            initialValue = confetti.startX,
            targetValue = confetti.endX,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ).value

        confetti.offsetY = infiniteTransition.animateFloat(
            initialValue = confetti.startY,
            targetValue = confetti.endY,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ).value
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        confettiStateList.forEach { confetti ->
            drawCircle(
                color = confetti.color,
                radius = 5f,
                center = Offset(confetti.offsetX, confetti.offsetY)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewConfetti() {
    BarbershopTheme {
        ConfettiAnimation()
    }
}

data class ConfettiState(
    var startPosition: Offset,
    val color: Color = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta).random()
) {
    val startX = startPosition.x
    val startY = startPosition.y

    private val angle = (0..360).random() * (PI / 180)

    val endX = startX + (100..500).random() * cos(angle).toFloat()
    val endY = startY + (100..500).random() * sin(angle).toFloat()

    var offsetX by mutableStateOf(startX)
    var offsetY by mutableStateOf(startY)
}