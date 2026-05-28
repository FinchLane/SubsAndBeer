package com.example.barbershop.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.sub.SubUtils.getSubWord
import com.example.barbershop.viewmodel.subscription.SubViewModel
import kotlin.isNaN

@Composable
fun ListCurrencyScreen(
    navController: NavController,
    subViewModel: SubViewModel,
    modifier: Modifier = Modifier
) {
    val currencyGroups = subViewModel.subscription
        .filter { !it.isArchive }
        .groupingBy { it.currency }
        .eachCount()
        .toList()
        .sortedByDescending { (_, count) -> count }

    val totalCurrency = currencyGroups.sumOf { it.second }

    val basicCurrency = subViewModel.uiState.basicCurrency

    Scaffold(
        topBar = {
            AppBarBack(
                nav = {navController.popBackStack()},
                title = "Распределение по валютам",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ){
            Text(
                text = "Всего валют - ${currencyGroups.count()}",
                style = MaterialTheme.typography.titleSmall
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                itemsIndexed(currencyGroups) { index, item ->
                    val isFirst = index == 0
                    val isLast = index == currencyGroups.lastIndex
                    val percent = (item.second.toDouble() / totalCurrency * 100).let {
                        if (it.isNaN()) 0.0 else it
                    }

                    CurrencyItem(
                        currency = item.first,
                        basicCurrency = basicCurrency,
                        countText = getSubWord(item.second),
                        percentage = "%.1f".format(percent),
                        onClick = { navController.navigate("currencyChart/${item.first}/$percent") },
                        isFirst = isFirst,
                        isLast = isLast
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyItem(
    currency: String,
    basicCurrency: String,
    countText: String,
    percentage: String,
    onClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(cornerRadius)
        isFirst -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RectangleShape
    }

    Surface(
        onClick = { onClick() },
        shape = shape,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column{
                Row {
                    Text(
                        text = currency,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (currency == basicCurrency) {
                        Text(
                            text = " (Главная)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}