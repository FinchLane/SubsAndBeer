package com.example.barbershop.ui.components.customComponent

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputContainer(
    title: String,
    title2: String,
    textFieldTitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    maskTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        Text(
            text = title2,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            value = value.ifEmpty {""},
            onValueChange = { input ->
                val numbersOnly = input.filter { it.isDigit() }
                onValueChange(numbersOnly)
            },
            label = { Text(textFieldTitle) },
            leadingIcon = leadingIcon,
            singleLine = true,
            readOnly = true,
            visualTransformation = maskTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.None
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, end = 36.dp)
                .clickable { keyboardController?.hide() }
        )
    }
}

@Preview("LightTheme")
@Preview("DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InputPreview() {
    BarbershopTheme {
        TextInputContainer(
            stringResource(R.string.login_account),
            stringResource(R.string.enter_phone_number),
            stringResource(R.string.phone_number),
            "9998887766",
            {},
            PhoneNumberMaskTransformation(),
            leadingIcon = {
                Text(text = "+7")
            }
        )
    }
}

class PhoneNumberMaskTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val mask = "000 000 0000"
        val trimmed = text.text.take(10)

        val annotatedString  = buildAnnotatedString {
            var index = 0
            for (char in mask) {
                if (char == '0') {
                    if (index < trimmed.length) {
                        append(
                            AnnotatedString(
                                trimmed[index].toString(),
                            )
                        )
                        index++
                    } else {
                        append(
                            AnnotatedString(
                                "0",
                                spanStyle = SpanStyle(color = Color.Gray)
                            )
                        )
                    }
                } else {
                    append(char)
                }
            }
        }

        return TransformedText(
            annotatedString,
            PhoneNumberOffsetMapping(mask, trimmed.length)
        )
    }
}

class PhoneNumberOffsetMapping(private val mask: String, private val originalTextLength: Int) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return 0
        var transformedOffset = offset
        for (i in mask.indices) {
            if (i >= transformedOffset) break
            if (mask[i] != '0') transformedOffset++
        }
        return transformedOffset.coerceAtMost(mask.length)
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 0) return 0
        var originalOffset = offset
        for (i in 0 until offset) {
            if (i >= mask.length || originalOffset <= 0) break
            if (mask[i] != '0') originalOffset--
        }
        return originalOffset.coerceIn(0, originalTextLength)
    }
}
