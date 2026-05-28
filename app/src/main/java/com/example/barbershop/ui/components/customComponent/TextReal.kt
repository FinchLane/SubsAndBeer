package com.example.barbershop.ui.components.customComponent

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun TextReal(
    number: Double,
    modifier: Modifier = Modifier,
    integerColor: Color = MaterialTheme.colorScheme.onBackground,
    fractionalColor: Color = MaterialTheme.colorScheme.onSurface,
    integerStyle: TextStyle = LocalTextStyle.current,
    fractionalStyle: TextStyle = LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * 0.8f)
) {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        decimalSeparator = '.'
        groupingSeparator = ' '
    }

    val formatter = DecimalFormat("#,##0.00", symbols)
    val formatted = formatter.format(number)

    val parts = formatted.split('.')
    val integerPart = parts.getOrElse(0) { "" }
    val fractionalPart = parts.getOrElse(1) { "00" }

    Row(modifier = modifier) {
        Text(
            text = integerPart,
            color = integerColor,
            style = integerStyle
        )
        Text(
            text = ".$fractionalPart",
            color = fractionalColor,
            style = fractionalStyle
        )
    }
}