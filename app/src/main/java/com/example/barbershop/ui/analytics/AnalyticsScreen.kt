package com.example.barbershop.ui.analytics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.ui.components.ScreenTitle
import com.example.barbershop.ui.components.customComponent.TextReal
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.viewmodel.subscription.SubViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.abs

@Composable
fun AnalyticsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    subViewModel: SubViewModel
) {
    val basicCurrency = subViewModel.uiState.basicCurrency
    val subscription = subViewModel.subscription.filter { !it.isArchive }
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
    val aveExp = converterSubscriptions.fold(listOf(0.0, 0.0, 0.0)) { total, sub ->
        val expenses = aveExpenses(sub.amount, sub.interval, sub.period)
        listOf(
            total[0] + expenses[0],
            total[1] + expenses[1],
            total[2] + expenses[2]
        )
    }

    val scrollState = rememberScrollState()

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            ScreenTitle("Аналитика", {})

            if (subscription.isNotEmpty()){
                Spacer(Modifier.height(16.dp))

                AverageExpenses(
                    basicCurrency,
                    aveExp
                )

                Spacer(Modifier.height(16.dp))

                ChartsSub(
                    converterSubscriptions,
                    onClick = {navController.navigate("totalSub")}
                )

                Spacer(Modifier.height(16.dp))

                ChartCurrency(
                    converterSubscriptions,
                    basicCurrency,
                    onClick = {
                        navController.navigate("listCurrency")
                    }
                )

                Spacer(Modifier.height(24.dp))

                CategoryList(
                    categories = subViewModel.categories,
                    subscriptions = converterSubscriptions,
                    navController = navController
                )

                Spacer(Modifier.height(24.dp))

                PaymentMethodList(
                    paymentMethods = subViewModel.paymentMethods,
                    subscriptions = converterSubscriptions,
                    navController = navController
                )

                Spacer(Modifier.height(100.dp))
            }
            else {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "У вас нет подписок",
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
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AverageExpenses(
    currency: String,
    aveExp: List<Double>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = "Средние траты, ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = currency,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "В неделю",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextReal(
                        number = aveExp[0]
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "В месяц",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextReal(
                        number = aveExp[1]
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "В год",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextReal(
                        number = aveExp[2]
                    )
                }
            }
        }
    }
}

@Composable
fun ChartsSub(
    subscription: List<Subscription>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPieIndex by remember { mutableIntStateOf(-1) }
    val backgroundColor = MaterialTheme.colorScheme.background

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = "Топ-3 подписки",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(16.dp))

            val (pieData, total) = remember(subscription) {
                val sortedSubs = subscription.sortedByDescending { it.amount }
                val top = sortedSubs.take(3)
                val others = sortedSubs.drop(3)
                val othersSum = others.sumOf { it.amount }

                val colors = generateColors(top.size, backgroundColor)

                val data = top.mapIndexed { index, sub ->
                    Pie(
                        label = sub.name,
                        data = sub.amount,
                        color = colors[index],
                        selectedColor = colors[index].adjustContrast(backgroundColor)
                    )
                }.toMutableList()

                if (othersSum > 0) {
                    data.add(
                        Pie(
                            label = "Другие подписки",
                            data = othersSum,
                            color = Color.Gray,
                            selectedColor = Color.DarkGray
                        )
                    )
                }

                Pair(data, data.sumOf { it.data })
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .fillMaxWidth()
                ) {
                    val animatedPercent by animateFloatAsState(
                        targetValue = pieData.getOrNull(selectedPieIndex)?.let {
                            (it.data / total * 100).toFloat()
                        } ?: 0f,
                        animationSpec = tween(300)
                    )

                    if (selectedPieIndex != -1) {
                        Text(
                            text = "%.1f%%".format(animatedPercent),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    PieChart(
                        modifier = Modifier.fillMaxSize(),
                        data = pieData.mapIndexed { index, pie ->
                            pie.copy(selected = index == selectedPieIndex)
                        },
                        onPieClick = { clickedPie ->
                            val newIndex = pieData.indexOfFirst { it.label == clickedPie.label }
                            selectedPieIndex = if (newIndex == selectedPieIndex) -1 else newIndex
                        },
                        selectedScale = 1.2f,
                        scaleAnimEnterSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        spaceDegree = 2f,
                        selectedPaddingDegree = 4f,
                        style = Pie.Style.Stroke(width = 32.dp),
                        colorAnimEnterSpec = tween(300),
                        colorAnimExitSpec = tween(300)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(pieData) { pie ->
                    val index = pieData.indexOf(pie)
                    val isSelected = index == selectedPieIndex
                    val percent = (pie.data / total * 100).let {
                        if (it.isNaN()) 0.0 else it
                    }

                    LegendItem(
                        color = pie.color,
                        label = pie.label.toString(),
                        percentage = "%.1f".format(percent),
                        isSelected = isSelected,
                        onClick = {
                            selectedPieIndex = if (isSelected) -1 else index
                        }
                    )
                }
            }

            Spacer(modifier.height(8.dp))

            Button(
                onClick = { onClick() },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inverseOnSurface),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Смотреть все",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun ChartCurrency(
    subscriptions: List<Subscription>,
    basicCurrency: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCurrencyIndex by remember { mutableIntStateOf(-1) }
    val backgroundColor = MaterialTheme.colorScheme.background

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = "Зарубежная валюта",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(16.dp))

            val (pieData, totalCount) = remember(subscriptions) {
                val currencyGroups = subscriptions
                    .groupingBy { it.currency }
                    .eachCount()
                    .toList()
                    .sortedByDescending { (_, count) -> count }

                val topCurrencies = currencyGroups.take(5)
                val others = currencyGroups.drop(5)
                val othersCount = others.sumOf { (_, count) -> count }

                val colors = generateColors(topCurrencies.size, backgroundColor)

                val data = topCurrencies.mapIndexed { index, (currency, count) ->
                    Pie(
                        label = currency.ifEmpty { "Без валюты" },
                        data = count.toDouble(),
                        color = colors[index],
                        selectedColor = colors[index].adjustContrast(backgroundColor)
                    )
                }.toMutableList()

                if (othersCount > 0) {
                    data.add(
                        Pie(
                            label = "Другие валюты",
                            data = othersCount.toDouble(),
                            color = Color.Gray,
                            selectedColor = Color.DarkGray
                        )
                    )
                }

                Pair(data, data.sumOf { it.data })
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .fillMaxWidth()
                ) {
                    val animatedPercent by animateFloatAsState(
                        targetValue = pieData.getOrNull(selectedCurrencyIndex)?.let {
                            (it.data / totalCount * 100).toFloat()
                        } ?: 0f,
                        animationSpec = tween(300)
                    )

                    if (selectedCurrencyIndex != -1) {
                        Text(
                            text = "%.1f%%".format(animatedPercent),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    PieChart(
                        modifier = Modifier.fillMaxSize(),
                        data = pieData.mapIndexed { index, pie ->
                            pie.copy(selected = index == selectedCurrencyIndex)
                        },
                        onPieClick = { clickedPie ->
                            val newIndex = pieData.indexOfFirst { it.label == clickedPie.label }
                            selectedCurrencyIndex = if (newIndex == selectedCurrencyIndex) -1 else newIndex
                        },
                        selectedScale = 1.2f,
                        scaleAnimEnterSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        spaceDegree = 2f,
                        selectedPaddingDegree = 4f,
                        style = Pie.Style.Stroke(width = 32.dp),
                        colorAnimEnterSpec = tween(300),
                        colorAnimExitSpec = tween(300)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(pieData) { pie ->
                    val index = pieData.indexOf(pie)
                    val isSelected = index == selectedCurrencyIndex
                    val percent = (pie.data / totalCount * 100).let {
                        if (it.isNaN()) 0.0 else it
                    }

                    LegendItem(
                        color = pie.color,
                        label = if (basicCurrency == pie.label) "${pie.label} (Основная)" else pie.label ?: "",
                        percentage = "%.1f".format(percent),
                        isSelected = isSelected,
                        onClick = {
                            selectedCurrencyIndex = if (isSelected) -1 else index
                        }
                    )
                }
            }

            Spacer(modifier.height(8.dp))

            Button(
                onClick = { onClick() },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inverseOnSurface),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Смотреть все",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun CategoryList(
    categories: List<Category>,
    subscriptions: List<Subscription>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val totalAmount = subscriptions.sumOf { it.amount }
    val withoutCategoryAmount = subscriptions
        .filter { it.categoryId == null }
        .sumOf { it.amount }
    val withoutCategoryPercentage = if (totalAmount > 0) {
        withoutCategoryAmount / totalAmount * 100
    } else {
        0.0
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Категории",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .heightIn(max = 1000.dp)
        ) {
            itemsIndexed(categories) { index, item ->
                val isFirst = index == 0

                val categoryAmount = subscriptions
                    .filter { it.categoryId == item.id }
                    .sumOf { it.amount }

                val percentage = if (totalAmount > 0) {
                    categoryAmount / totalAmount * 100
                } else {
                    0.0
                }

                StatItem(
                    name = item.name,
                    percentage = "%.1f".format(percentage),
                    onClick = {navController.navigate("categoryChart/${item.id}")},
                    isFirst = isFirst,
                    isLast = false
                )
            }
        }
        StatItem(
            name = "Без категории",
            percentage = "%.1f".format(withoutCategoryPercentage),
            onClick = { navController.navigate("categoryChart/0") },
            isFirst = categories.isEmpty(),
            isLast = true,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun PaymentMethodList(
    paymentMethods: List<PaymentMethod>,
    subscriptions: List<Subscription>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val totalAmount = subscriptions.sumOf { it.amount }
    val withoutPaymentMethodAmount = subscriptions
        .filter { it.paymentMethodId == null }
        .sumOf { it.amount }

    val withoutPaymentMethodPercentage = if (totalAmount > 0) {
        withoutPaymentMethodAmount / totalAmount * 100
    } else {
        0.0
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Платежные методы",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .heightIn(max = 1000.dp),
        ) {
            itemsIndexed(paymentMethods) { index, item ->
                val isFirst = index == 0

                val paymentMethodAmount = subscriptions
                    .filter { it.paymentMethodId == item.id }
                    .sumOf { it.amount }

                val percentage = if (totalAmount > 0) {
                    paymentMethodAmount / totalAmount * 100
                } else {
                    0.0
                }

                StatItem(
                    name = item.name,
                    percentage = "%.1f".format(percentage),
                    onClick = { navController.navigate("paymentMethodChart/${item.id}") },
                    isFirst = isFirst,
                    isLast = false
                )
            }
        }
        StatItem(
            name = "Без категории",
            percentage = "%.1f".format(withoutPaymentMethodPercentage),
            onClick = { navController.navigate("paymentMethodChart/0") },
            isFirst = paymentMethods.isEmpty(),
            isLast = true,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    percentage: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(200)
    )

    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) color else color.copy(alpha = 0.7f),
        animationSpec = tween(300)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .alpha(animatedAlpha)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(animatedColor, CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (isSelected) 1f else 0.8f
            )
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (isSelected) 1f else 0.8f
            )
        )
    }
}

@Composable
fun StatItem(
    name: String,
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
        shape = shape,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row {
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

private fun generateColors(count: Int, background: Color): List<Color> {
    return List(count) { i ->
        var baseColor = Color.hsl(
            hue = (i * 360f / count) % 360f,
            saturation = 0.7f,
            lightness = 0.5f
        )

        if (!baseColor.hasSufficientContrast(background)) {
            baseColor = baseColor.copy(alpha = 0.8f)
        }

        baseColor
    }
}

private fun Color.hasSufficientContrast(background: Color): Boolean {
    val fgLuminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
    val bgLuminance = 0.2126 * background.red + 0.7152 * background.green + 0.0722 * background.blue
    return abs(fgLuminance - bgLuminance) > 0.3
}

private fun Color.adjustContrast(background: Color): Color {
    return if (hasSufficientContrast(background)) this else {
        val contrastFactor = if (alpha < 0.5) 1.5f else 0.7f
        this.copy(alpha = (alpha * contrastFactor).coerceIn(0.2f, 1f))
    }
}