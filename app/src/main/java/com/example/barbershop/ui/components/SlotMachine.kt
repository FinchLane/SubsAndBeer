package com.example.barbershop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.barbershop.ui.ducks.GIFImage
import com.example.barbershop.ui.theme.BarbershopTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SlotMachine(
    emojis: List<Int>,
    spinDuration: Long,
    stopDelay: Long,
    stopTime: Long,
    modifier: Modifier = Modifier
) {
    val slots = remember { Array(4) { mutableIntStateOf(emojis.random()) } }
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(true) }
    var showEasterEgg by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false)}
    var slotPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    LaunchedEffect(Unit) {
        while (true){
            isSpinning = true
            showConfetti = false
            for (i in slots.indices){
                scope.launch {
                    while (isSpinning){
                        slots[i].intValue = emojis.random()
                        delay(100)
                    }
                }
            }

            delay(spinDuration)
            slots.forEachIndexed { index, slot ->
                delay(stopDelay)
                slot.intValue = emojis.random()
            }

            isSpinning = false
            if (checkSymbolsMatch(slots)){
                showEasterEgg = true
                showConfetti = true
            }
            else{
                showEasterEgg = false
            }

            delay(stopTime)
        }
    }

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned { coordiantes ->
                    slotPosition = coordiantes.positionInParent()
                }
        ) {
            slots.forEach { slot ->
                SlotItem(imageRes = slot.intValue.toString())
            }
        }

        if (showConfetti) {
            ConfettiAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                startPosition = slotPosition
            )
        }
    }

}

@Composable
fun SlotItem(
    imageRes: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = imageRes,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
    //GIFImage(data = imageRes.toInt(), modifier = Modifier.size(22.dp))
}

fun checkSymbolsMatch(slots: Array<MutableIntState>): Boolean {
    return slots.all { it.intValue == slots[0].intValue }
}

//@Preview
//@Composable
//private fun PreviewSlot() {
//    BarbershopTheme {
//        SlotMachine(
//            emojis = listOf("1", "2", "3", "4", "5"),
//            spinDuration = 3000L,
//            stopDelay = 10000L,
//            stopTime = 3000L
//        )
//    }
//}