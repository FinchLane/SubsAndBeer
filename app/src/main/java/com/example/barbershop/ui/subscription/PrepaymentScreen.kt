package com.example.barbershop.ui.subscription

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.TextInputField
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.components.notification.ClickableTextField
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.formatDate
import com.example.barbershop.utils.sub.SubUtils.formatNextPaymentDate
import com.example.barbershop.utils.sub.SubUtils.getDayWord
import com.example.barbershop.utils.sub.SubUtils.getMonthWord
import com.example.barbershop.utils.sub.SubUtils.getWeekWord
import com.example.barbershop.utils.sub.SubUtils.getYearWord
import com.example.barbershop.viewmodel.subscription.SubViewModel
import java.time.LocalDate

@Composable
fun PrepaymentScreen(
    navController: NavController,
    subViewModel: SubViewModel,
    modifier: Modifier = Modifier,
    id: String = ""
) {
    val navigationEvent by subViewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "home" -> {
                navController.navigate("home")
                subViewModel.clearNavigationEvent()
            }

            else -> Unit
        }
    }

    val subscribe = subViewModel.subscription.first { it.id == id }
    var count by remember { mutableIntStateOf(1) }
    val nextPaymentDate = when (subscribe.period) {
        "Год" -> subscribe.nextPaymentDate.plusYears(count.toLong())
        "Месяц" -> subscribe.nextPaymentDate.plusMonths(count.toLong())
        "Неделя" -> subscribe.nextPaymentDate.plusWeeks(count.toLong())
        else -> subscribe.nextPaymentDate.plusDays(count.toLong())
    }

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}, title = "Предоплата") },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                TitleCard(
                    image = subscribe.iconSource,
                    color = Color(subscribe.backgroundColor),
                    name = subscribe.name,
                    nextPayment = formatNextPaymentDate(subscribe.nextPaymentDate)
                )
                Spacer(Modifier.height(16.dp))
                PaymentCard(
                    amount = subscribe.amount.toString(),
                    interval = subscribe.interval.toString(),
                    period = subscribe.period,
                    currency = subscribe.currency,
                    value = count.toString(),
                    onValueChange = { value ->
                        count = if (value.toInt() >= 1 && value != "") {
                            value.toInt()
                        } else {
                            1
                        }
                    },
                    onClickMinus = {
                        if (count > 1) {
                            count--
                        }
                    },
                    onClickPlus = { count++ },
                    total = (subscribe.amount * count).toString()
                )
                Spacer(Modifier.height(16.dp))
                ResultCard(
                    days = subscribe.interval * count,
                    period = subscribe.period,
                    nextPaymentDate = nextPaymentDate
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { subViewModel.updateSubscription(subscribe.copy(nextPaymentDate = nextPaymentDate)) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Добавить время",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun TitleCard(
    image: String,
    color: Color,
    name: String,
    nextPayment: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AdaptiveIcon(
                imageSrc = image,
                tintColor = color,
                backgroundColor = color,
                modifier = Modifier
                    .size(64.dp),
                containerShape = RoundedCornerShape(20.dp),
                containerSize = 64.dp,
                iconSize = 44.dp,
                contentAlignment = Alignment.Center
            )
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = nextPayment
                )
            }
        }
    }
}

@Composable
fun PaymentCard(
    amount: String,
    interval: String,
    period: String,
    currency: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClickMinus: () -> Unit,
    onClickPlus: () -> Unit,
    total: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Стоимость",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Стоимость"
                )
                Text(
                    text = amount + getSymbolForCurrency(currency) + " за " + interval + " " + period.lowercase()
                )
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Количество"
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.weight(2f)
                ) {
                    ClickableTextField(
                        value = "-",
                        onClick = { onClickMinus() },
                        textAlign = TextAlign.Center,
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                        modifier = Modifier.weight(2f)
                    )
                    TextInputField(
                        value = value,
                        onValueChange = onValueChange,
                        textAlign = TextAlign.Center,
                        singleLine = true,
                        keyboardType = KeyboardType.Number,
                        shape = RoundedCornerShape(0),
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 2.dp)
                    )
                    ClickableTextField(
                        value = "+",
                        onClick = { onClickPlus() },
                        textAlign = TextAlign.Center,
                        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Итого"
                )
                Text(
                    text = total + " " + getSymbolForCurrency(currency)
                )
            }
        }
    }
}

@Composable
fun ResultCard(
    days: Int,
    period: String,
    nextPaymentDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Результат",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Добавлено"
                )
                Text(
                    text = "+ $days " + when (period) {
                        "Год" -> getYearWord(days)
                        "Месяц" -> getMonthWord(days)
                        "Неделя" -> getWeekWord(days)
                        else -> getDayWord(days)
                    }
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Следующий платеж"
                )
                Text(
                    text = formatNextPaymentDate(nextPaymentDate).lowercase()
                )
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Новая дата платежа"
                )
                Text(
                    text = formatDate(nextPaymentDate)
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleCardPreview() {
    BarbershopTheme {
        TitleCard(
            image = "",
            color = Color(0xff14a9a9),
            name = "Наименование",
            nextPayment = "Платеж через 7 дней"
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PaymentCardPreview() {
    BarbershopTheme {
        PaymentCard(
            amount = "650",
            interval = "1",
            period = "месяц",
            currency = "RUB",
            value = "1",
            onValueChange = { },
            onClickMinus = { },
            onClickPlus = { },
            total = "650"
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ResultCardPreview() {
    BarbershopTheme {
        ResultCard(
            days = 30,
            period = "месяц",
            nextPaymentDate = LocalDate.now()
        )
    }
}