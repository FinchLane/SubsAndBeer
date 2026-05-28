package com.example.barbershop.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    )
    {
        for (i in 0 until 4){
            SingleDigitBox(
                digit = code.getOrNull(i)?.toString() ?: "",
                isActive = i == code.length,
                isCompleted = i < code.length,
                onDigitEntered = {digit ->
                    if (code.length < 4){
                        onCodeChange(code + digit)
                    }
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CodeInputPreview() {
    CodeInput("", {})
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SingleDigitBox(
    digit: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onDigitEntered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val borderColor = when {
        isCompleted -> Color.Green
        isActive -> Color.White
        else -> Color.Gray
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
//            .background(
//                color = if (isCompleted) Color.Green.copy(alpha = 0.2f) else Color.Transparent,
//                shape = RoundedCornerShape(8.dp)
//            )
            .clickable(enabled = false) { }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ){
        if (isActive) {
            TextField(
                value = digit,
                onValueChange = {input ->
                    if (input.length == 1 && input[0].isDigit()) {
                        onDigitEntered(input)
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Center
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                interactionSource = MutableInteractionSource(),
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.clickable { keyboardController?.hide() }
            )
        }
        else {
            Text(
                text = digit,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}