package com.example.barbershop.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun PaymentMethodChartScreen(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    paymentMethodId: String = "0"
) {
    val basicCurrency = subViewModel.uiState.basicCurrency

    val subscription = subViewModel.subscription
            .filter {
                !it.isArchive &&
                if (paymentMethodId != "0") it.paymentMethodId == paymentMethodId
                else it.paymentMethodId.isNullOrEmpty()
            }
            .map { sub ->
                if (sub.currency != basicCurrency) {
                    val convertedAmount = convertSubscriptionAmount(
                        sub.amount,
                        sub.currency,
                        basicCurrency,
                        subViewModel
                    )
                    sub.copy(amount = convertedAmount)
                } else {
                    sub
                }
            }
            .sortedByDescending { aveExpenses(it.amount, it.interval, it.period)[0] }

    val symbol = getSymbolForCurrency(basicCurrency)

    var totalPeriod by remember { mutableStateOf("неделя") }
    var avePeriod = when (totalPeriod) {
        "неделя" -> 0
        "месяц" -> 1
        else -> 2
    }

    val total = subscription.sumOf {
        aveExpenses(it.amount, it.interval, it.period)[avePeriod]
    }

    Scaffold(
        topBar = {
            AppBarBack(
                nav = {navController.popBackStack()},
                title = if (paymentMethodId != "0") subViewModel.paymentMethods.first {it.id == paymentMethodId}.name else "Без платежного метода",
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
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (subscription.isNotEmpty()) {
                ChartAveItem(
                    subscription = subscription,
                    percent = 100.0,
                    periodIndex = avePeriod
                )
                Text(
                    text = "Всего подписок - ${subscription.count()}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(subscription) { index, item ->
                        val isFirst = index == 0
                        val isLast = index == subscription.lastIndex
                        val aveAmount = aveExpenses(item.amount, item.interval, item.period)
                        val percent = (aveAmount[avePeriod] / total * 100).let {
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
            else {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Нет подписок у данного платежного метода",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TextButton(
                    onClick = {navController.navigate("addSub")}
                ) {
                    Text(
                        text = "Добавить подписку",
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}