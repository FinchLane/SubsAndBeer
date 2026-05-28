package com.example.barbershop.ui.subscription

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.Constants.BASE_URl
import com.example.barbershop.R
import com.example.barbershop.data.model.subscription.template.TemplatePlans
import com.example.barbershop.ui.components.DatePickerDialogSample
import com.example.barbershop.ui.components.DropDownSpinner
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.TextInputField
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.components.notification.ClickableTextField
import com.example.barbershop.ui.subscription.bottomSheet.IconBottomSheet
import com.example.barbershop.ui.subscription.bottomSheet.NotificationBottomSheet
import com.example.barbershop.ui.subscription.bottomSheet.TemplatePlanBottomSheet
import com.example.barbershop.ui.subscription.category.AddCategoryDialog
import com.example.barbershop.ui.subscription.category.CategoryBottomSheet
import com.example.barbershop.ui.subscription.currency.CurrencyBottomSheet
import com.example.barbershop.ui.subscription.paymentMethod.AddPaymentMethodDialog
import com.example.barbershop.ui.subscription.paymentMethod.PaymentMethodBottomSheet
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.utils.sub.SubUtils.calculateNextDate
import com.example.barbershop.utils.sub.SubUtils.formatDate
import com.example.barbershop.viewmodel.subscription.SubViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun AddSubScreen(
    navController: NavController,
    viewModel: SubViewModel,
    modifier: Modifier = Modifier,
    id: String? = null,
    templateId: Int? = null
) {

    val navigationEvent by viewModel.navigateTo.collectAsState()
    val uiState = viewModel.uiState

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "home" -> {
                navController.navigate("home")
                viewModel.clearNavigationEvent()
            }
            else -> Unit
        }
    }

//    LaunchedEffect(Unit) {
//        viewModel.clearData()
//    }

    LaunchedEffect(templateId) {
        viewModel.resetSelectedPlan()
    }

    id?.let {
        LaunchedEffect(it) {
            viewModel.loadSubscription(it)
        }
    }

    templateId?.let {
        LaunchedEffect(it) {
            val template = viewModel.templates.first { it.id == templateId }

            val templatePlans = viewModel.plans.filter { it.templateId == templateId }

            val initialPlan = templatePlans.getOrNull(uiState.selectedPlan) ?: templatePlans.first()

            viewModel.updateSubscriptionName(template.name)
            viewModel.updateSelectedIcon(BASE_URl + template.iconSource)
            viewModel.updateSelectionColor(Color.Transparent)

            viewModel.updateCancelURL(template.cancelUrl ?: "")
            viewModel.updateAmount(initialPlan.amount.toInt().toString())
            viewModel.updateInterval(initialPlan.interval.toString())
            viewModel.updateSelectedPeriod(initialPlan.period)

            viewModel.updateTemplatePlans(templatePlans)
        }
    }

    val formattedStartDate = formatDate(uiState.startDate)
    val nextDate = calculateNextDate(
        interval = uiState.interval,
        selectedPeriod = uiState.selectedPeriod,
        startDate = uiState.startDate
    )
    val formattedNextDate = formatDate(nextDate)
    Scaffold(
        topBar = {
            AppBarBack(
                nav = {
                    navController.popBackStack()
                    viewModel.clearData()
                      },
                title = if (id == null) "Новая подписка" else "Изменение подписки",
                content = {
                    IconButton(
                        onClick = {
                            if (uiState.name.isEmpty() && uiState.amount.isEmpty()) {
                                if (uiState.name.isEmpty()) {
                                    nameError = true
                                }

                                if (uiState.amount.isEmpty()) {
                                    amountError = true
                                }
                            }
                            else {
                                viewModel.saveSubscription()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val filteredPlans = viewModel.plans.filter { it.templateId == templateId }
                val currentPlan = filteredPlans.getOrNull(viewModel.uiState.selectedPlan)

                NameSub(
                    value = uiState.name,
                    onValueChange = {
                        viewModel.updateSubscriptionName(it)
                        nameError = false
                                    },
                    image = uiState.selectedIcon,
                    color = uiState.selectionColor,
                    onClick = { viewModel.openIconBottomSheet() },
                    onClickPlan = { viewModel.openTemplatePLanBottomSheet() },
                    plan = currentPlan,
                    isError = nameError
                )
                Spacer(Modifier.height(16.dp))
                PaySub(
                    payValue = uiState.amount,
                    payOnValueChange = {
                        viewModel.updateAmount(it)
                        amountError = false
                                       },
                    period = uiState.interval,
                    periodOnValueChange = { viewModel.updateInterval(it) },
                    currencyValue = uiState.selectedCurrency,
                    exchangeRate = uiState.customRate,
                    exchangeRateValueChange = { viewModel.updateCustomRate(it) },
                    periodList = uiState.periodList,
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = { viewModel.updateSelectedPeriod(it) },
                    onClickCurrency = { viewModel.openCurrencyBottomSheet() },
                    currencyName = uiState.availableCurrencies.first {
                        it.id == uiState.selectedCurrency
                    }.name,
                    isError = amountError
                )
                Spacer(Modifier.height(16.dp))
                DateSub(
                    formattedStartDate,
                    { viewModel.openDatePicker() },
                    { viewModel.openNotificationBottomSheet() },
                    formattedNextDate,
                    viewModel.infoNotification(),
                    if (viewModel.infoNotification() == "Выключены") "Выбрать" else "Изменить"
                )
                Spacer(Modifier.height(16.dp))
                StatSub(
                    { viewModel.openCategory() },
                    viewModel.categories.find { category ->
                        category.id == uiState.selectedCategoryId
                    }?.name ?: "Не выбрана",
                    { viewModel.openPaymentMethod() },
                    viewModel.paymentMethods.find { paymentMethod ->
                        paymentMethod.id == uiState.selectedPaymentMethodId
                    }?.name ?: "Не выбран"
                )
                Spacer(Modifier.height(16.dp))
                CancelSubURL(
                    urlValue = uiState.cancelURL,
                    urlOnValueChange = { viewModel.updateCancelURL(it) }
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (uiState.name.isEmpty() && uiState.amount.isEmpty()) {
                            if (uiState.name.isEmpty()) {
                                nameError = true
                            }

                            if (uiState.amount.isEmpty()) {
                                amountError = true
                            }
                        }
                        else {
                            viewModel.saveSubscription()
                        }
                              },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Сохранить")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (uiState.showIconBottomSheet) {
        IconBottomSheet(
            onDismissRequest = { viewModel.closeIconBottomSheet() },
            onIconSelected = { iconRes -> viewModel.updateSelectedIcon(iconRes.toImageResourceString()) },
            onColorSelected = { color -> viewModel.updateSelectionColor(color) }
        )
    }

    if (uiState.showTemplatePlanBottomSheet) {
        val filteredPlans by remember(viewModel.templatePlans) {
            derivedStateOf { viewModel.templatePlans }
        }

        TemplatePlanBottomSheet(
            plans = filteredPlans,
            selectedPlan = uiState.selectedPlan,
            onPlanSelect = { index ->
                viewModel.updateSelectedPlan(index)
                filteredPlans.getOrNull(index)?.let { plan ->
                    viewModel.updateAmount(plan.amount.toInt().toString())
                    viewModel.updateInterval(plan.interval.toString())
                    viewModel.updateSelectedPeriod(plan.period)
                }
            },
            onDismissRequest = { viewModel.closeTemplatePLanBottomSheet() }
        )
    }

    if (uiState.showDatePicker) {
        DatePickerDialogSample(
            onDateSelected = { dateMillis ->
                if (dateMillis != null) {
                    val selectedDate = Instant.ofEpochMilli(dateMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    viewModel.updateStartDate(selectedDate)
                }
            },
            onDismiss = { viewModel.closeDatePicker() }
        )
    }

    if (uiState.showNotificationBottomSheet) {
        NotificationBottomSheet(
            checkboxes = uiState.notificationCheckboxes,
            onStateChange = { id, value ->
                viewModel.updateState(id, value)
            },
            onDismissRequest = { viewModel.closeNotificationBottomSheet() }
        )
    }

    if (uiState.showCategoryBottomSheet) {
        CategoryBottomSheet(
            categories = viewModel.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = { categoryId -> viewModel.updateSelectedCategory(categoryId) },
            onDismissRequest = { viewModel.closeCategoryBottomSheet() },
            onClick = {
                navController.navigate("category")
                viewModel.closeCategoryBottomSheet()
            }
        )
    }

    if (uiState.showDialogCategory) {
        AddCategoryDialog(
            value = uiState.nameCategory,
            onValueChange = { viewModel.updateCategoryName(it) },
            onClick = {
                viewModel.saveCategory(uiState.nameCategory)
                viewModel.closeDialogCategory()
            },
            onDismiss = { viewModel.closeDialogCategory() }
        )
    }

    if (uiState.showPaymentMethodBottomSheet) {
        PaymentMethodBottomSheet(
            paymentMethods = viewModel.paymentMethods,
            selectedPaymentMethodId = uiState.selectedPaymentMethodId,
            onPaymentMethodSelected = { paymentMethodId ->
                viewModel.updateSelectedPaymentMethod(paymentMethodId)
            },
            onDismissRequest = { viewModel.closePaymentMethodBottomSheet() },
            onClick = {
                navController.navigate("paymentMethod")
                viewModel.closePaymentMethodBottomSheet()
            }
        )
    }

    if (uiState.showDialogPaymentMethod) {
        AddPaymentMethodDialog(
            value = uiState.namePaymentMethod,
            onValueChange = { viewModel.updateNamePaymentMethod(it) },
            onClick = {
                viewModel.savePaymentMethod(uiState.namePaymentMethod)
                viewModel.closeDialogPaymentMethod()
            },
            onDismiss = { viewModel.closeDialogPaymentMethod() }
        )
    }

    if (uiState.showCurrencyBottomSheet) {
        CurrencyBottomSheet(
            currencies = viewModel.getBottomSheetCurrencies(),
            selectedCurrency = uiState.selectedCurrency,
            onCurrencySelected = { viewModel.selectCurrency(it) },
            onClickManage = { navController.navigate("currency") },
            onDismissRequest = { viewModel.closeCurrencyBottomSheet() }
        )
    }
}

@Composable
fun NameSub(
    value: String,
    onValueChange: (String) -> Unit,
    image: String,
    color: Color,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onClickPlan: () -> Unit,
    plan: TemplatePlans? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(164.dp)
            ) {
                AdaptiveIcon(
                    imageSrc = image,
                    tintColor = color,
                    backgroundColor = color,
                    onClick = onClick,
                    containerShape = RoundedCornerShape(20.dp),
                    containerSize = 160.dp,
                    iconSize = 96.dp,
                    contentAlignment = Alignment.Center
                )
                if (plan == null) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onClick() }
                    )
                }
            }
            TextInputField(
                value = value,
                onValueChange = onValueChange,
                showCounter = true,
                maxLength = 64,
                placeholder = "Название подписки",
                singleLine = false,
                isError = isError,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            if (plan != null) {
                ClickableTextField(
                    value = plan.name,
                    onClick = { onClickPlan() },
                    title = "ПЛАН",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PaySub(
    payValue: String,
    payOnValueChange: (String) -> Unit,
    period: String,
    periodOnValueChange: (String) -> Unit,
    currencyValue: String,
    exchangeRateValueChange: (String) -> Unit,
    periodList: List<String>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    onClickCurrency: () -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    currencyName: String? = null,
    exchangeRate: String? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Платеж",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextInputField(
                    value = payValue,
                    onValueChange = payOnValueChange,
                    placeholder = "199",
                    title = "СУММА",
                    keyboardType = KeyboardType.Number,
                    isError = isError,
                    modifier = Modifier.weight(2f)
                )
                Spacer(Modifier.width(16.dp))
                ClickableTextField(
                    value = currencyValue,
                    onClick = {onClickCurrency()},
                    title = "ВАЛЮТА",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                                   },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Каждые",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    TextInputField(
                        value = period,
                        onValueChange = periodOnValueChange,
                        keyboardType = KeyboardType.Number,
                        placeholder = "1"
                    )
                }
                Spacer(Modifier.width(16.dp))
                DropDownSpinner(
                    selectedItem = selectedPeriod,
                    onItemSelect = {_, item ->
                        onPeriodSelected(item)
                    },
                    itemList = periodList,
                    defaultText = "Месяц",
                    modifier = Modifier.weight(1f)
                )
            }
            if (currencyValue != "RUB"){
                Text(
                    text = "КУРС ВАЛЮТЫ",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "1 $currencyValue",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currencyName ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.arrow_2),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                    )
                    TextInputField(
                        value = exchangeRate ?: "",
                        onValueChange = exchangeRateValueChange,
                        keyboardType = KeyboardType.Number,
                        placeholder = "0.0",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DateSub(value: String, onClick: () -> Unit, onClickButton: () -> Unit, nextDate: String, notificationText: String, textButton: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Дата",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            ClickableTextField(
                title = "НАЧАЛЬНАЯ ДАТА",
                value = value,
                onClick = onClick,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
            Text(
                text = "Следущая дата платежа - $nextDate",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Уведомления",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = notificationText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Button(
                    onClick = {onClickButton()},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary.copy(0.7f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = textButton,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun StatSub(
    onClickCategory: () -> Unit,
    titleCategory: String,
    onClickPaymentMethod: () -> Unit,
    titlePaymentMethod: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Статистика",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Категория",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = titleCategory,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Button(
                    onClick = {onClickCategory()},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary.copy(0.7f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Выбрать",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Метод оплаты",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = titlePaymentMethod,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Button(
                    onClick = {onClickPaymentMethod()},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary.copy(0.7f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Выбрать",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CancelSubURL(
    urlValue: String,
    urlOnValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ){
            Row {
                Text(
                    text = "Ссылка на отмену подписки",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(16.dp))
            TextInputField(
                value = urlValue,
                onValueChange = urlOnValueChange,
                placeholder = "https://... (необязательно)",
                keyboardType = KeyboardType.Uri
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NameSubPreview() {
    BarbershopTheme {
        NameSub("", {}, image = R.drawable.m0nesy_featured_image.toImageResourceString(), color = Color(0xff14a9a9).copy(0.2f), onClick = {}, onClickPlan = {}, isError = false)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PaySubPreview() {
    BarbershopTheme {
        PaySub("", {}, "", {}, "USD", {} ,listOf("Месяц", "Год"),  "", {}, {}, currencyName = "Доллар США", isError = false)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DateSubPreview() {
    BarbershopTheme {
        DateSub("7 февраля 2025", {}, {}, "", "Выключены", "Выбрать")
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatSubPreview() {
    BarbershopTheme {
        StatSub({}, "Не выбрана", {}, "")
    }
}

