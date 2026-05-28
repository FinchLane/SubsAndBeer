package com.example.barbershop.ui.subscription

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.ScreenTitle
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.GeneralRadioButton
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.notification.RequestNotificationPermissionIfNeeded
import com.example.barbershop.utils.sub.SubUtils.formatNextPaymentDate
import com.example.barbershop.viewmodel.subscription.SubViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SubViewModel = hiltViewModel(),
) {
    RequestNotificationPermissionIfNeeded()
    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "newSub" -> {
                navController.navigate("newSub")
                viewModel.clearNavigationEvent()
            }
            "addSub" -> {
                navController.navigate("addSub")
                viewModel.clearNavigationEvent()
            }

            else -> Unit
        }
    }
    var showCategoryDialog by remember { mutableStateOf(false) }
    val titles = listOf("Активные", "Архивные")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier.fillMaxSize()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            ScreenTitle(
                "Подписки",
                {
                    IconButton(
                        onClick = { showCategoryDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SortByAlpha,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                },
                modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp))
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                titles.forEachIndexed{index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> ActiveScreen(
                        navController,
                        viewModel
                    )
                    1 -> ArchiveScreen(
                        navController,
                        viewModel
                    )
                }
            }
        }
        if (showCategoryDialog) {
            SortedDialog(
                selectedSorted =  viewModel.uiState.selectedSorted,
                onSortedSelected = {id ->
                    viewModel.updateSelectedSorted(id)
                },
                onDismiss = { showCategoryDialog = false }
            )
        }
    }
}

@Composable
fun ActiveScreen(
    navController: NavController,
    viewModel: SubViewModel,
    modifier: Modifier = Modifier
) {
    val subscriptions = viewModel.subscription
        .filter { !it.isArchive }
        .let { list ->
            when (viewModel.uiState.selectedSorted) {
                1 -> list.sortedBy { it.nextPaymentDate }
                2 -> list.sortedByDescending { it.amount }
                else -> list.sortedBy { it.amount }
            }
        }
    LaunchedEffect(subscriptions) {
        Timber.tag("ActiveScreen").d("Subscriptions updated: $subscriptions")
    }
    Box{
        if (subscriptions.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Нет подписок",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "У вас нет активных подписок.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(subscriptions) { index, item ->
                    val isFirst = index == 0
                    val isLast = index == subscriptions.lastIndex
                    SubscriptionItem(
                        image = item.iconSource,
                        color = Color(item.backgroundColor),
                        nextPaymentDate = formatNextPaymentDate(item.nextPaymentDate, viewModel, item),
                        name = item.name,
                        payment = item.amount.toInt().toString(),
                        symbol = getSymbolForCurrency(item.currency),
                        interval = item.interval.toString(),
                        period = item.period,
                        onClick = { navController.navigate("subInfo/${item.id}") },
                        isFirst = isFirst,
                        isLast = isLast
                    )
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
        Button(
            onClick = {
                viewModel.getTemplates()
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 104.dp, end = 16.dp)
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: SubViewModel,
    modifier: Modifier = Modifier
) {
    val subscriptions = viewModel.subscription
        .filter { it.isArchive }
        .let { list ->
            when (viewModel.uiState.selectedSorted) {
                1 -> list.sortedBy { it.nextPaymentDate }
                2 -> list.sortedByDescending { it.amount }
                else -> list.sortedBy { it.amount }
            }
        }
    Box {
        if (subscriptions.isEmpty()){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = "Нет подписок",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "У вас нет подписок в архиве.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(subscriptions) { index, item ->
                    val isFirst = index == 0
                    val isLast = index == subscriptions.lastIndex
                    SubscriptionItem(
                        image = item.iconSource,
                        color = Color(item.backgroundColor),
                        nextPaymentDate = formatNextPaymentDate(item.nextPaymentDate, viewModel, item),
                        name = item.name,
                        payment = item.amount.toInt().toString(),
                        symbol = getSymbolForCurrency(item.currency),
                        interval = item.interval.toString(),
                        period = item.period,
                        onClick = { navController.navigate("subInfo/${item.id}") },
                        isFirst = isFirst,
                        isLast = isLast
                    )
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun AnimatedExtendedFloatingActionButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        },
        text = {
            if (expanded) {
                Text(text = "Добавить")
            }
        }
    )
}

@Composable
fun SubscriptionItem(
    image: String,
    color: Color,
    nextPaymentDate: String,
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
        onClick = onClick,
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
                    text = nextPaymentDate,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "$payment$symbol за $interval ${period.lowercase()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SortedDialog(
    selectedSorted: Int,
    onSortedSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radioOptions = mapOf(1 to "По дате платежа (сначала ближайшие)", 2 to "По цене (сначала дорогие)", 3 to "По цене (сначала дешевые)")

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Сортировка списка",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(16.dp))
                Column(modifier.selectableGroup()) {
                    radioOptions.forEach { (index, text) ->
                        GeneralRadioButton(
                            text = text,
                            value = index,
                            selectedOption = selectedSorted,
                            onOptionSelect = onSortedSelected
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            text = "Выбрать"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SubscriptionItemPreview() {
    BarbershopTheme {
        SubscriptionItem(
            image = R.drawable.m0nesy_featured_image.toImageResourceString(),
            color = Color(0xff14a9a9).copy(0.2f),
            nextPaymentDate = "Через 11 месяцев",
            name = "Тестовая подписка",
            payment = "1990",
            symbol = "₽",
            interval = "1",
            period = "Год",
            {},
            false,
            false
        )
    }
}
