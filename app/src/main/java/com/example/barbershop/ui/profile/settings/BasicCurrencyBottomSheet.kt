package com.example.barbershop.ui.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.ui.components.customComponent.TextInputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCurrencyBottomSheet(
    currencies: List<Currency>,
    basicCurrency: String,
    onClickItem: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery = remember { mutableStateOf("") }
    val filteredCurrencies = currencies.filter {
        val query = searchQuery.value.trim().lowercase()
        query.isEmpty() || it.name.lowercase().contains(query) || it.id.lowercase().contains(query)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            TextInputField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = "Поиск валюты",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                background = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (filteredCurrencies.isEmpty()) {
                Text(
                    text = "Ничего не найдено",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            else {
                LazyColumn {
                    itemsIndexed(filteredCurrencies.filter { it.id != "XDR" }){index, currency ->
                        val shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            currencies.size - 1 -> RoundedCornerShape(
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                            else -> RoundedCornerShape(5.dp)
                        }
                        BasicCurrencyItem(
                            currency = currency,
                            shape = shape,
                            onSelect = currency.id == basicCurrency,
                            onClick = {
                                onClickItem(currency.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BasicCurrencyItem(
    currency: Currency,
    shape: Shape,
    onSelect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(vertical = 2.dp),
        shape = shape,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = currency.id,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (onSelect){
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}