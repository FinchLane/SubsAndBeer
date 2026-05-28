package com.example.barbershop.ui.subscription

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.barbershop.R
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.TextReal
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.subscription.bottomSheet.ActionBottomSheet
import com.example.barbershop.ui.subscription.bottomSheet.ConfirmationDeleteSubscription
import com.example.barbershop.ui.subscription.category.AddCategoryDialog
import com.example.barbershop.ui.subscription.category.CategoryBottomSheet
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.utils.currency.convertSubscriptionAmount
import com.example.barbershop.utils.currency.getSymbolForCurrency
import com.example.barbershop.utils.sub.SubUtils.aveExpenses
import com.example.barbershop.utils.sub.SubUtils.daysLeft
import com.example.barbershop.utils.sub.SubUtils.formatDate
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun SubScreen(
    id: String,
    viewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navigationEvent by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "home" -> {
                navController.navigate("home")
                viewModel.clearNavigationEvent()
            }

            else -> Unit
        }
    }

    val context = LocalContext.current
    val basicCurrency = viewModel.uiState.basicCurrency
    val converterSubscriptions = viewModel.subscription.map { subscription ->
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
    if (viewModel.uiState.showCategoryBottomSheet){
        CategoryBottomSheet(
            categories = viewModel.categories,
            selectedCategoryId = viewModel.uiState.selectedCategoryId,
            onCategorySelected = { categoryId ->
                viewModel.updateSelectedCategory(categoryId)
            },
            onDismissRequest = {viewModel.closeCategoryBottomSheet()},
            onClick = {
                navController.navigate("category")
                viewModel.closeCategoryBottomSheet()
            }
        )
    }

    if (viewModel.uiState.showDialogCategory){
        AddCategoryDialog(
            value = viewModel.uiState.nameCategory,
            onValueChange = {viewModel.updateCategoryName(it)},
            onClick = {
                viewModel.saveCategory(viewModel.uiState.nameCategory)
                viewModel.closeDialogCategory()
            },
            onDismiss = {viewModel.closeDialogCategory()}
        )
    }

    if (viewModel.uiState.showActionBottomSheet){
        ActionBottomSheet(
            onEditClick = {
                navController.navigate("editSub/${subscribe.id}")
                viewModel.closeActionBottomSheet()
            },
            onArchiveClick = {
                viewModel.archiveSubscription(subscribe.id)
                viewModel.closeActionBottomSheet()
            },
            onDeleteClick = {
                viewModel.openDeleteMessage()
                viewModel.closeActionBottomSheet()
            },
            onDismissRequest = { viewModel.closeActionBottomSheet() }
        )
    }

    if (viewModel.uiState.showDeleteMessage){
        ConfirmationDeleteSubscription(
            onClick = { viewModel.removeSubscription(subscribe.id) },
            onDismiss = { viewModel.closeDeleteMessage() }
        )
    }

    if (viewModel.uiState.selectedCategoryId != subscribe.categoryId && viewModel.uiState.selectedCategoryId != "-1"){
        subscribe.categoryId = viewModel.uiState.selectedCategoryId
        viewModel.updateSubscription(subscribe)
        viewModel.closeDialogCategory()
        viewModel.closeCategoryBottomSheet()
    }
}

@Composable
fun SubInfo(
    isArchive: Boolean,
    color: Color,
    img: String,
    name: String,
    payValue: Int,
    symbol: String,
    interval: Int,
    period: String,
    category: String,
    percent: Int,
    nextPay: String,
    daysLeft: String,
    onClickCategory: () -> Unit,
    onClickPrepayment: () -> Unit,
    onClickAction: () -> Unit,
    onClickEdit: () -> Unit,
    onClickUnarchive: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
    percentCategory: Int? = null
) {
    val alpha = if (isArchive) 0.3f else 1f
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AdaptiveIcon(
                    imageSrc = img,
                    tintColor = color,
                    backgroundColor = color,
                    modifier = Modifier.size(64.dp),
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
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "$payValue$symbol за $interval ${period.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Box {
                if (isArchive){
                    ArchiveLabelExample(modifier = Modifier.align(Alignment.Center))
                }
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .alpha(alpha)
                    ) {
                        if (category.isEmpty()) {
                            Text(
                                text = "выбрать\nкатегорию",
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .height(100.dp)
                                    .weight(1f)
                                    .clickable { onClickCategory() }
                                    .wrapContentSize(Alignment.Center)
                            )
                        } else {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$percentCategory%",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "в категории\n$category",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        VerticalDivider(Modifier.height(100.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "от всех\nрасходов",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            if (!isArchive) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "следующий платеж\n$nextPay"
                    )
                    Text(
                        text = "через $daysLeft"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {onClickPrepayment()},
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Предоплата",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(Modifier.width(2.dp))
                    Button(
                        onClick = {onClickAction()},
                        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dehaze,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Действия",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            else{
                ActionButton(
                    onClickEdit,
                    onClickUnarchive,
                    onClickDelete
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SubInfoPreview() {
    BarbershopTheme {
        SubInfo(
            isArchive = false,
            color = Color(0xff14a9a9).copy(0.2f),
            img = R.drawable.m0nesy_featured_image.toImageResourceString(),
            name = "ВПН сервис",
            payValue = 1999,
            symbol = "₽",
            interval = 1,
            period = "год",
            category = "",
            percent = 100,
            nextPay = "9 мая 2025",
            daysLeft = "81 день",
            onClickCategory = {},
            onClickPrepayment = {},
            onClickAction = {},
            percentCategory = 100,
            onClickEdit = {},
            onClickUnarchive = {},
            onClickDelete = {}
        )
    }
}

@Composable
fun StatSub(
    payWeek: Double,
    payMonth: Double,
    payYear: Double,
    basicCurrency: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Средние расходы, $basicCurrency",
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
                        number = payWeek
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
                        number = payMonth
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
                        number = payYear
                    )
                }
            }
//            Button(
//                onClick = {},
//                shape = MaterialTheme.shapes.small,
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp)
//            ) {
//                Text(
//                    text = "Открыть аналитику",
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatSubPreview() {
    BarbershopTheme {
        StatSub(38.27, 25.6, 199.0, "RUB")
    }
}

@Composable
fun ActionButton(
    onClickEdit: () -> Unit,
    onClickUnarchive: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {onClickEdit()},
            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Изменить",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(Modifier.width(2.dp))
        Button(
            onClick = onClickUnarchive,
            shape = RoundedCornerShape(0),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Filled.Unarchive,
                    contentDescription = null,
                    tint = Color(0xffbf7e1d)
                )
                Text(
                    text = "Разархив.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(Modifier.width(2.dp))
        Button(
            onClick = {onClickDelete()},
            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
                Text(
                    text = "Удалить",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ArchiveLabelExample(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .zIndex(1f)
            .graphicsLayer(
                rotationZ = -15f
            )
            .background(Color(0xffbf7e1d), RoundedCornerShape(20.dp))
    ) {
        Text(
            text = "В архиве",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)

        )
    }
}