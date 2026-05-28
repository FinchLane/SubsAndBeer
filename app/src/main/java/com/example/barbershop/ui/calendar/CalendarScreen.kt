package com.example.barbershop.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.ui.components.ScreenTitle
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.shouldDisplaySubscriptionOnDate
import com.example.barbershop.viewmodel.subscription.SubViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    subViewModel: SubViewModel = hiltViewModel(),
) {

    val currentDate = LocalDate.now()

    var displayDate by remember { mutableStateOf(currentDate.withDayOfMonth(1)) }
    var isCurrentMonth = displayDate.year == currentDate.year && displayDate.month == currentDate.month


    val basicCurrency = subViewModel.uiState.basicCurrency

    val originalSubscriptions = subViewModel.subscription.filter { !it.isArchive }

    val convertedAmounts = remember(originalSubscriptions, basicCurrency) {
        originalSubscriptions.associate { sub ->
            sub.id to if (sub.currency != basicCurrency) {
                convertSubscriptionAmount(
                    sub.amount,
                    sub.currency,
                    basicCurrency,
                    subViewModel
                )
            } else {
                sub.amount
            }
        }
    }

    val subscriptions = originalSubscriptions
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val filteredList by remember(selectedDate, displayDate) {
        derivedStateOf {
            if (selectedDate == null) {
                subscriptions.filter { subscription ->
                    (1..displayDate.lengthOfMonth()).any { day ->
                        val dateInMonth = displayDate.withDayOfMonth(day)
                        shouldDisplaySubscriptionOnDate(subscription, dateInMonth)
                    }
                }
            } else {
                subscriptions.filter { subscription ->
                    shouldDisplaySubscriptionOnDate(subscription, selectedDate!!)
                }.sortedBy { it.startDate }
            }
        }
    }

    val textMonth = if (selectedDate != null) {
        selectedDate!!.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")))
    } else {
        when {
            displayDate.month == currentDate.month && displayDate.year == currentDate.year -> "В этом месяце"
            YearMonth.of(displayDate.year, displayDate.month) == YearMonth.of(currentDate.year, currentDate.month).plusMonths(1) -> "В следующем месяце"
            else -> displayDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))
                .replaceFirstChar { it.titlecase() } + " ${displayDate.year}"
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            ScreenTitle(
                title = "Календарь", {}
            )
            Spacer(Modifier.height(16.dp))
            CustomCalendar(
                displayedDate = displayDate,
                isCurrentMonth = isCurrentMonth,
                onPrevMonth = {
                    displayDate = displayDate.minusMonths(1)
                    selectedDate = null
                },
                onNextMonth = {
                    displayDate = displayDate.plusMonths(1)
                    selectedDate = null
                },
                year = displayDate.year,
                month = displayDate.monthValue,
                currentDate = currentDate,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = if (selectedDate == date) null else date
                },
                subscriptions = originalSubscriptions,
                onMonthTitleClick = {selectedDate = null}
            )
            if (filteredList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = textMonth,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = " – ${filteredList.sumOf { convertedAmounts[it.id] ?: 0.0 }.toInt()} $basicCurrency",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    itemsIndexed(filteredList) { index, item ->
                        val isFirst = index == 0
                        val isLast = index == filteredList.lastIndex
                        SubscriptionCalendarItem(
                            image = item.iconSource,
                            color = Color(item.backgroundColor),
                            name = item.name,
                            payment = item.amount.toInt().toString(),
                            symbol = getSymbolForCurrency(item.currency),
                            interval = item.interval.toString(),
                            period = item.period,
                            onClick = {navController.navigate("subInfo/${item.id}")},
                            isFirst = isFirst,
                            isLast = isLast
                        )
                    }
                    item{
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
            else {
                Text(
                    text = stringResource(R.string.no_payments_this_month),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun MonthSelector(
    displayedDate: LocalDate,
    isCurrentMonth: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthTitleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onPrevMonth,
            enabled = !isCurrentMonth
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Предыдущий месяц",
                tint = if (isCurrentMonth) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = displayedDate.month.getDisplayName(
                TextStyle.FULL_STANDALONE,
                Locale.forLanguageTag("ru")
            ).replaceFirstChar { it.titlecase() } + " ${displayedDate.year}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {onMonthTitleClick()}
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Следующий месяц",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CustomCalendar(
    displayedDate: LocalDate,
    isCurrentMonth: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    year: Int,
    month: Int,
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthTitleClick: () -> Unit,
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value

    val daysList = (1..daysInMonth).map { day ->
        firstDayOfMonth.withDayOfMonth(day)
    }

    Surface(
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            MonthSelector(
                displayedDate = displayedDate,
                isCurrentMonth = isCurrentMonth,
                onPrevMonth = { onPrevMonth()},
                onNextMonth = { onNextMonth() },
                onMonthTitleClick = { onMonthTitleClick() }
            )
            DaysOfWeekHeader()
            HorizontalDivider()
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = modifier.heightIn(min = 200.dp)
            ) {
                items(startDayOfWeek - 1) {
                    Spacer(modifier = Modifier.size(40.dp))
                }

                items(daysList) { date ->
                    val isToday = date == currentDate
                    val isSelected = date == selectedDate

                    val paymentsForDate = subscriptions.filter { shouldDisplaySubscriptionOnDate(it, date) }
                    val hasPayment = paymentsForDate.isNotEmpty()

                    val backgroundColor = when {
                        isToday -> MaterialTheme.colorScheme.primary.copy(0.2f)
                        isSelected -> MaterialTheme.colorScheme.onBackground.copy(0.5f)
                        else -> Color.Transparent
                    }

                    val textColor = when {
                        isSelected -> MaterialTheme.colorScheme.inverseOnSurface
                        else -> MaterialTheme.colorScheme.onBackground
                    }


                    Box(
                        modifier = if (hasPayment) {
                            Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onDateSelected(date) }
                        } else {
                            Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        if (hasPayment && date != selectedDate) {
                            Box(
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                val firstPayment = paymentsForDate.first()

                                AdaptiveIcon(
                                    imageSrc = firstPayment.iconSource,
                                    tintColor = Color(firstPayment.backgroundColor),
                                    backgroundColor = Color(firstPayment.backgroundColor),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(16.dp),
                                    containerShape = RoundedCornerShape(5.dp),
                                    containerSize = 16.dp,
                                    iconSize = 12.dp,
                                    contentAlignment = Alignment.Center,
                                    onClick = { onDateSelected(date) }
                                )

                                if (paymentsForDate.size > 1) {
                                    Text(
                                        text = "+${paymentsForDate.size - 1}",
                                        color = textColor,
                                        fontSize = 8.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 4.dp, y = (-2).dp)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader(
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf(
        "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        daysOfWeek.forEach { dayName ->
            Text(
                text = dayName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SubscriptionCalendarItem(
    image: String,
    color: Color,
    name: String,
    payment: String,
    symbol: String,
    interval: String,
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
        shape = shape,
        onClick = { onClick() },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "$payment$symbol за $interval ${period.lowercase()}"
                )
            }
        }
    }
}