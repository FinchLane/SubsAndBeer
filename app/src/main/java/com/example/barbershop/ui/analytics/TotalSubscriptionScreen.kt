package com.example.barbershop.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun TotalSubscription(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val basicCurrency = subViewModel.uiState.basicCurrency
    val subscription = subViewModel.subscription.filter { !it.isArchive }.sortedByDescending { it.amount }
    val converterSubscriptions = subscription.map { subscription ->
        if (subscription.currency != basicCurrency) {
            val convertedAmount = convertSubscriptionAmount(
                subscription.amount,
                subscription.currency,
                basicCurrency,
                subViewModel
            )
            subscription.copy(amount = convertedAmount)
        } else {
            subscription
        }
    }

    val symbol = getSymbolForCurrency(basicCurrency)

    var totalPeriod by remember { mutableStateOf("неделя") }
    var avePeriod = when (totalPeriod) {
        "неделя" -> 0
        "месяц" -> 1
        else -> 2
    }
    val total = converterSubscriptions.sumOf { it.amount }


    Scaffold(
        topBar = {
            AppBarBack(
                nav = {navController.popBackStack()},
                title = "Подписки",
                content = {
                    IconButton(
                        onClick = {
                            totalPeriod = when (totalPeriod) {
                                "неделя" -> "месяц"
                                "месяц" -> "год"
                                else -> "неделя"
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding).padding(16.dp)
        ){
            Text(
                text = "Всего подписок - ${subscription.count()}",
                style = MaterialTheme.typography.titleSmall
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(converterSubscriptions) { index, item ->
                    val isFirst = index == 0
                    val isLast = index == subscription.lastIndex
                    val aveAmount = aveExpenses(item.amount, item.interval, item.period)
                    val percent = (item.amount/ total * 100).let {
                        if (it.isNaN()) 0.0 else it
                    }

                    TotalSubItem(
                        image = item.iconSource,
                        color = Color(item.backgroundColor),
                        name = item.name,
                        amount = aveAmount[avePeriod],
                        symbol = symbol,
                        percentage = "%.1f".format(percent),
                        period = totalPeriod,
                        onClick = { navController.navigate("subInfo/${item.id}") },
                        isFirst = isFirst,
                        isLast = isLast
                    )
                }
            }
        }
    }
}

@Composable
fun TotalSubItem(
    image: String,
    color: Color,
    name: String,
    amount: Double,
    symbol: String,
    percentage: String,
    period: String,
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
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                AdaptiveIcon(
                    imageSrc = image,
                    tintColor = color,
                    backgroundColor = color,
                    modifier = Modifier.size(48.dp),
                    containerShape = RoundedCornerShape(16.dp),
                    containerSize = 48.dp,
                    iconSize = 32.dp,
                    contentAlignment = Alignment.Center
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "%.1f".format(amount) +"$symbol в $period",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}