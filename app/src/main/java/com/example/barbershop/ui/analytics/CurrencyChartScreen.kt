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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.viewmodel.subscription.SubViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.abs

@Composable
fun CurrencyChartScreen(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    currency: String = "RUB",
    percent: Double = 100.0
) {
    val basicCurrency = subViewModel.uiState.basicCurrency

    val subscription = subViewModel.subscription
            .filter { it.currency == currency && !it.isArchive }
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
            .sortedByDescending { it.amount }

    val symbol = getSymbolForCurrency(basicCurrency)

    var totalPeriod by remember { mutableStateOf("неделя") }
    var avePeriod = when (totalPeriod) {
        "неделя" -> 0
        "месяц" -> 1
        else -> 2
    }

    val total = subscription.sumOf { it.amount }

    Scaffold(
        topBar = {
            AppBarBack(
                nav = {navController.popBackStack()},
                title = currency,
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
        ) {
            ChartItem(
                subscription = subscription.filter { it.currency == currency },
                percent = percent
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
                    val percent = (item.amount/ total * percent).let {
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
fun ChartItem(
    subscription: List<Subscription>,
    percent: Double,
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
//            Text(
//                text = "Топ-3 подписки",
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//
//            Spacer(Modifier.height(16.dp))

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
                            (it.data / total * percent).toFloat()
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
                    val percent = (pie.data / total * percent).let {
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
        }
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