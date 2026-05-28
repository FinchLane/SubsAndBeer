package com.example.barbershop.ui.subscription.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconBottomSheet(
    onDismissRequest: () -> Unit,
    onIconSelected: (Int) -> Unit,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember { mutableStateOf(Color(0xff14a9a9).copy(0.2f)) }

    var selectedIcon by remember { mutableIntStateOf(R.drawable.ico_20) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Стиль иконки",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Выбор цвета
            Text(
                text = "Цвет",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Color(0xffbf7e1d).copy(0.2f),
                    Color(0xffa63a19).copy(0.2f),
                    Color(0xff526aec).copy(0.2f),
                    Color(0xff14a9a9).copy(0.2f),
                    Color(0xff0f8f24).copy(0.2f)
                ).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color, RoundedCornerShape(20))
                            .clickable {
                                selectedColor = color
                                onColorSelected(color)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == color) {
                            Image(
                                painter = painterResource(id = R.drawable.check),
                                contentDescription = "Выбранный цвет",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Иконка",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(20) { index ->
                    val iconRes = getIconResource(index + 1)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                            .clickable {
                                selectedIcon = iconRes
                                onIconSelected(iconRes)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedIcon == iconRes) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onBackground, //selectedColor?.copy(alpha = 1f) ?: Color.Black,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(selectedColor, RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).align(Alignment.Center),
                                colorFilter = selectedColor.let { color ->
                                    ColorFilter.tint(color.copy(alpha = 1f))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getIconResource(index: Int): Int {
    return when (index) {
        1 -> R.drawable.ico_1
        2 -> R.drawable.ico_2
        3 -> R.drawable.ico_3
        4 -> R.drawable.ico_4
        5 -> R.drawable.ico_5
        6 -> R.drawable.ico_6
        7 -> R.drawable.ico_7
        8 -> R.drawable.ico_8
        9 -> R.drawable.ico_9
        10 -> R.drawable.ico_10
        11 -> R.drawable.ico_11
        12 -> R.drawable.ico_12
        13 -> R.drawable.ico_13
        14 -> R.drawable.ico_14
        15 -> R.drawable.ico_15
        16 -> R.drawable.ico_16
        17 -> R.drawable.ico_17
        18 -> R.drawable.ico_18
        19 -> R.drawable.ico_19
        20 -> R.drawable.ico_20
        else -> R.drawable.ico_1
    }
}

@Preview
@Composable
private fun ModalBottomSheetPreview() {
    BarbershopTheme {
        IconBottomSheet(
            onDismissRequest = {},
            onIconSelected = {},
            onColorSelected = {}
        )
    }
}