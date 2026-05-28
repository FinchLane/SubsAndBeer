package com.example.barbershop.ui.subscription.paymentMethod

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.barbershop.ui.components.customComponent.TextInputField
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun AddPaymentMethodDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    onDismiss: () -> Unit, modifier:
    Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Добавить платежный метод",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Text(
                    text = "Платежные методы используются для классификации и дополнительной статистики",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextInputField(
                    value = value,
                    onValueChange = onValueChange,
                    showCounter = true,
                    maxLength = 32,
                    placeholder = "Карта, мобильный телефон и т.п. "
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Отмена"
                        )
                    }
                    TextButton(
                        onClick = onClick
                    ) {
                        Text(
                            text = "Сохранить"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddCategoryPreview() {
    BarbershopTheme {
        AddPaymentMethodDialog("", {}, {}, {})
    }
}