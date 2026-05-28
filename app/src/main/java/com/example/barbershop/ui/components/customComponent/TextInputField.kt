package com.example.barbershop.ui.components.customComponent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ELEMENT_HEIGHT = 48.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(), // По умолчанию занимает всю ширину
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    imeAction: ImeAction? = null,
    keyboardActions: KeyboardActions? = null,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Start,
    height: Dp = ELEMENT_HEIGHT,
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE, // Максимальная длина ввода
    title: String? = null,
    showCounter: Boolean = false, // Показывать ли счетчик символов
    shape: Shape = MaterialTheme.shapes.medium, // Форма для закругления краев
    label: @Composable (() -> Unit)? = null, // Лейбл для текстового поля
    leadingIcon: @Composable (() -> Unit)? = null, // Иконка в начале
    trailingIcon: @Composable (() -> Unit)? = null, // Иконка в конце
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(4.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            singleLine = singleLine,
            textStyle = TextStyle(
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = textAlign
            ),
            keyboardActions = keyboardActions ?: KeyboardActions(
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onSearch = { focusManager.clearFocus() }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = imeAction ?: if (singleLine) ImeAction.Done else ImeAction.Default
            ),
            interactionSource = interactionSource,
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester),
            readOnly = readOnly,
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .clip(shape)
                        .background(
                            color = if (isError) MaterialTheme.colorScheme.errorContainer else background
                        )
                        .height(height)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart // Выравнивание по центру вертикали
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        // Иконка в начале
                        if (leadingIcon != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart) // Выравнивание иконки по центру вертикали
                                    .padding(end = 8.dp) // Отступ между иконкой и текстом
                            ) {
                                leadingIcon()
                            }
                        }

                        // Текст и placeholder
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = if (leadingIcon != null) 32.dp else 0.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                    fontSize = fontSize,
                                    maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }

                        // Иконка в конце
                        if (trailingIcon != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd) // Выравнивание иконки по центру вертикали
                            ) {
                                trailingIcon()
                            }
                        }
                    }
                }
            },
            visualTransformation = visualTransformation
        )

        if (showCounter) {
            Text(
                text = "${value.length}/$maxLength",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.End),
                style = MaterialTheme.typography.bodySmall,
                color = if (value.length > maxLength) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
