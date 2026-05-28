package com.example.barbershop.ui.subscription.currency

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.ui.components.customComponent.TextInputField
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun CurrencyScreen(
    navController: NavController,
    viewModel: SubViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery = remember { mutableStateOf("") }
    val allCurrencies = viewModel.uiState.availableCurrencies.filter {
        val query = searchQuery.value.trim().lowercase()
        query.isEmpty() || it.name.lowercase().contains(query) || it.id.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            AppBarBack(
                nav = { navController.popBackStack() },
                title = "Валюта",
                content = {
                    IconButton(onClick = { viewModel.updateCurrenciesFromCBR() }) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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

            val usedCurrencies = viewModel.getUsedCurrencies()
            val popularCurrencies = listOf("RUB", "USD", "EUR")
                .mapNotNull { id -> allCurrencies.firstOrNull { it.id == id } }
                .filter { it !in usedCurrencies }
            val otherCurrencies = allCurrencies
                .filter { it !in usedCurrencies && it !in popularCurrencies }
                .filter {
                    it.name.contains(searchQuery.value, ignoreCase = true) ||
                            it.id.contains(searchQuery.value, ignoreCase = true)
                }

            LazyColumn {
                if (searchQuery.value.isEmpty()) {
                    if (usedCurrencies.isNotEmpty()) {
                        item {
                            Text(
                                "Используемые валюты",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        itemsIndexed(usedCurrencies) { index, currency ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                usedCurrencies.size - 1 -> RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                                else -> RoundedCornerShape(2.dp)
                            }
                            CurrencyItem(
                                currency = currency,
                                exchangeRate = viewModel.getExchangeRate("RUB", currency.id)?.rate ?: 1.0,
                                shape = shape,
                                onClick = {
                                    viewModel.selectCurrency(currency.id)
                                    viewModel.updateCurrencyBottomSheet(currency.id)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    if (popularCurrencies.isNotEmpty()) {
                        item {
                            Text(
                                "Популярные валюты",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        itemsIndexed(popularCurrencies) { index, currency ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                popularCurrencies.size - 1 -> RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                                else -> RoundedCornerShape(2.dp)
                            }
                            CurrencyItem(
                                currency = currency,
                                exchangeRate = viewModel.getExchangeRate("RUB", currency.id)?.rate ?: 1.0,
                                shape = shape,
                                onClick = {
                                    viewModel.selectCurrency(currency.id)
                                    viewModel.updateCurrencyBottomSheet(currency.id)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    if (otherCurrencies.isNotEmpty()) {
                        item {
                            Text(
                                "Все валюты",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        itemsIndexed(otherCurrencies.filter { it.id != "XDR" }) { index, currency ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                otherCurrencies.size - 1 -> RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                                else -> RoundedCornerShape(2.dp)
                            }
                            CurrencyItem(
                                currency = currency,
                                exchangeRate = viewModel.getExchangeRate("RUB", currency.id)?.rate ?: 1.0,
                                shape = shape,
                                onClick = {
                                    viewModel.selectCurrency(currency.id)
                                    viewModel.updateCurrencyBottomSheet(currency.id)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
                else {
                    itemsIndexed(allCurrencies.filter { it.id != "XDR" }) { index, currency ->
                        val shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            popularCurrencies.size - 1 -> RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                            else -> RoundedCornerShape(2.dp)
                        }
                        CurrencyItem(
                            currency = currency,
                            exchangeRate = viewModel.getExchangeRate("RUB", currency.id)?.rate ?: 1.0,
                            shape = shape,
                            onClick = {
                                viewModel.selectCurrency(currency.id)
                                viewModel.updateCurrencyBottomSheet(currency.id)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyItem(
    currency: Currency,
    exchangeRate: Double?,
    shape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
//    val currencySymbols = mapOf(
//        "RUB" to "₽",
//        "USD" to "$",
//        "EUR" to "€"
//    )
//
//    val symbol = currencySymbols[currency.id] ?: currency.id

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
                    text = "${currency.id} – ${currency.symbol}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (currency.id != "RUB") {
                Text(
                    text = String.format("%.2f RUB", exchangeRate),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}