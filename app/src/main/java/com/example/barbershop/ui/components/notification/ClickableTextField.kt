package com.example.barbershop.ui.components.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ELEMENT_HEIGHT = 48.dp

@Composable
fun ClickableTextField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    placeholder: String = "",
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Start,
    height: Dp = ELEMENT_HEIGHT,
    title: String? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(4.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(shape)
                .background(background)
                .height(height)
                .clickable { onClick() }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 8.dp)
                    ) {
                        leadingIcon()
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = if (leadingIcon != null) 32.dp else 0.dp)
                ) {
                    Text(
                        text = value.ifEmpty { placeholder },
                        color = if (value.isNotEmpty()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        fontSize = fontSize,
                        maxLines = 1,
                        textAlign = textAlign,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingIcon != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}