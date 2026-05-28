package com.example.barbershop.ui.subscription.currency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.ui.components.customComponent.GeneralRadioButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    currencies: List<Currency>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onClickManage: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Валюта",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {onClickManage()},
                    colors = ButtonDefaults.buttonColors(Color.DarkGray),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ЕЩЕ",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            currencies.forEach { currency ->
                GeneralRadioButton(
                    text = "${currency.id} — ${currency.name} (${currency.symbol})",
                    value = currency.id,
                    selectedOption = selectedCurrency,
                    onOptionSelect = onCurrencySelected
                )
            }
        }
    }
}
