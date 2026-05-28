package com.example.barbershop.ui.subscription

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.utils.sub.SubUtils.daysLeft
import com.example.barbershop.utils.sub.SubUtils.formatDate
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun SubAnalyticsScreen(
    id: String,
    viewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val basicCurrency = viewModel.uiState.basicCurrency
    val converterSubscriptions = remember(viewModel.subscription, basicCurrency) {
        viewModel.subscription.map { subscription ->
            if (subscription.currency != basicCurrency) {
                val convertedAmount = convertSubscriptionAmount(
                    subscription.amount,
                    subscription.currency,
                    basicCurrency,
                    viewModel
                )
                subscription.copy(amount = convertedAmount)
            } else {
                subscription
            }
        }
    }
    val subscribe = converterSubscriptions.firstOrNull { it.id == id }
    val originalSubscribe = viewModel.subscription.firstOrNull {it.id == id}
    if (subscribe?.id == null) {
        return // какая-то фигня, нужно исправить когда-нибудь
    }

    val categoryExpenses = converterSubscriptions.filter { it.categoryId == subscribe.categoryId }.sumOf { it.amount }
    val totalExpenses = converterSubscriptions.sumOf { it.amount }
    val subscriptionPercentageOfTotal = (subscribe.amount / totalExpenses) * 100
    val subscriptionPercentageOfCategory = (subscribe.amount / categoryExpenses) * 100
    val formattedNextDate = formatDate(subscribe.nextPaymentDate)
    val daysLeft = daysLeft(subscribe.nextPaymentDate)
    val stat = aveExpenses(subscribe.amount, subscribe.interval, subscribe.period)
    val color = Color(subscribe.backgroundColor)

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}, title = if (!subscribe.isArchive) "Подписка" else "Архивная подписка") },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SubInfo(
                    isArchive = subscribe.isArchive,
                    color = color,
                    img = subscribe.iconSource,
                    name = subscribe.name,
                    payValue = originalSubscribe?.amount?.toInt() ?: 0,
                    symbol = getSymbolForCurrency(subscribe.currency),
                    interval = subscribe.interval,
                    period = subscribe.period,
                    category = viewModel.categories.firstOrNull {it.id == subscribe.categoryId}?.name
                        ?: "",
                    percent = subscriptionPercentageOfTotal.toInt(),
                    nextPay = formattedNextDate,
                    daysLeft = daysLeft,
                    percentCategory = subscriptionPercentageOfCategory.toInt(),
                    onClickCategory = {viewModel.openCategory()},
                    onClickPrepayment = { navController.navigate("subPrepayment/${subscribe.id}") },
                    onClickAction = {viewModel.openActionBottomSheet()},
                    onClickEdit = {navController.navigate("editSub/${subscribe.id}")},
                    onClickUnarchive = {
                        viewModel.unarchiveSubscription(subscribe.id)
                    },
                    onClickDelete = {
                        viewModel.removeSubscription(subscribe.id)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                )
                StatSub(
                    payWeek = stat[0],
                    payMonth = stat[1],
                    payYear = stat[2],
                    basicCurrency = basicCurrency,
                    modifier = Modifier.padding(top = 16.dp)
                )
                if (!subscribe.cancelUrl.isNullOrBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, subscribe.cancelUrl!!.toUri())
                            context.startActivity(intent)
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Перейти на страницу отмены подписки"
                        )
                    }
                }
            }
        }
    }
}