package com.example.barbershop.ui.profile

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.Constants.BASE_URl
import com.example.barbershop.R
import com.example.barbershop.data.model.MessageEntity
import com.example.barbershop.data.model.ThemeType
import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.family.FamilyMemberEntity
import com.example.barbershop.ui.components.ScreenTitle
import com.example.barbershop.ui.components.TimePickerDialog
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.customComponent.PhoneNumberMaskTransformation
import com.example.barbershop.ui.components.customComponent.TextInputField
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.ui.profile.settings.BasicCurrencyBottomSheet
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.profile.ProfileViewModel
import com.example.barbershop.viewmodel.subscription.SubViewModel
import com.example.barbershop.viewmodel.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    subViewModel: SubViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {

    val baseCurrency by subViewModel.baseCurrencyFlow.collectAsState(initial = "RUB")
    val notificationTime by subViewModel.notificationTimeFlow.collectAsState(initial = "12:00")
    val currentTheme by themeViewModel.currentTheme
    val isAuthorized by viewModel.isAuth.collectAsState(initial = false)

    val messages by viewModel.messages.collectAsState(emptyList())
    val families by viewModel.families.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()

    val context = LocalContext.current
    val packageInfo = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName ?: "unknown"

    val scrollState = rememberScrollState()
    val image by viewModel.profileImage.collectAsState()

    var showCreateFamilyDialog by remember { mutableStateOf(false) }
    var showAddFamilyMemberDialog by remember { mutableStateOf(false) }
    var familyMemberNumber by remember { mutableStateOf("") }

    var showClearCategoryDialog by remember { mutableStateOf(false) }
    var showClearPaymentDialog by remember { mutableStateOf(false) }
    var showClearSubscriptionDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    if (isAuthorized) {
        LaunchedEffect(Unit) {
            viewModel.fetchProfileFromServer()
        }
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            ScreenTitle(
                "Профиль",
                content = {
                    IconButton(
                        onClick = {themeViewModel.toggleTheme()}
                    ) {
                        Icon(
                            imageVector = when (currentTheme) {
                                ThemeType.LIGHT -> Icons.Default.Nightlight
                                ThemeType.DARK -> Icons.Default.WbSunny
                            },
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate("notification") }
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                            if (messages.any { !it.isChecked }) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                )
                            }
                        }
                    }
                }
            )
            Spacer(Modifier.height(16.dp))

            if (isAuthorized) {
                UserProfileCard(
                    image = image,
                    viewModel.firstNameUser + " " + viewModel.lastNameUser,
                    "+${viewModel.phoneNumberUser}",
                    Modifier.clickable { navController.navigate("editProfile")}
                )
                Spacer(Modifier.height(16.dp))
                FamilyCard(
                    families = families,
                    familyMembers = familyMembers,
                    owner = viewModel.phoneNumberUser,
                    onAddMemberClick = {
                        showAddFamilyMemberDialog = true
                    },
                    onCreateFamilyClick = {
                        showCreateFamilyDialog = true
                    }
                )
            }
            else {
                AuthCard(
                    onClick = { navController.navigate("login")}
                )
            }
            Spacer(Modifier.height(16.dp))
            SettingsCard(
                onClickBaseCurrency = {subViewModel.openBasicCurrencyBottomSheet()},
                basicCurrency = baseCurrency,
                onClickTimeNotification = { subViewModel.openTimePicker() },
                timeNotification = notificationTime,
                onClickCategory = {navController.navigate("category")},
                onClickPaymentMethod = {navController.navigate("paymentMethod")},
                onClickTestNotification = {
                    val messageEntity = MessageEntity(
                        title = "Тестовое уведомление",
                        body = "Это пример уведомления",
                        screen = "",
                        screenId = ""
                    )
                    viewModel.insertMessage(messageEntity)
                }
            )
            Spacer(Modifier.height(16.dp))
            DataCard(
                onClickClearAllCategory = { showClearCategoryDialog = true },
                onClickClearAllPaymentMethod = { showClearPaymentDialog = true },
                onClickClearAllSubscription = { showClearSubscriptionDialog = true },
                onClickClearAllData = { showClearAllDialog = true }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Версия: $versionName; Сервер: $BASE_URl",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(100.dp))
        }
    }

    if (subViewModel.uiState.showBasicCurrencyBottomSheet){
        BasicCurrencyBottomSheet(
            currencies = subViewModel.uiState.availableCurrencies,
            basicCurrency = baseCurrency,
            onClickItem = {currency ->
                subViewModel.setBaseCurrency(currency)
                subViewModel.closeBasicCurrencyBottomSheet()
            },
            onDismissRequest = {subViewModel.closeBasicCurrencyBottomSheet()}
        )
    }

    if (subViewModel.uiState.showTimePicker) {
        TimePickerDialog(
            onConfirm = { timePickerState ->
                val hour = timePickerState.hour
                val minute = timePickerState.minute
                val timeString = "%02d:%02d".format(hour, minute)

                subViewModel.setNotificationTime(timeString)
                subViewModel.closeTimePicker()
                subViewModel.scheduleAllNotifications()
            },
            onDismiss = {
                subViewModel.closeTimePicker()
            }
        )
    }

    if (showClearCategoryDialog) {
        ConfirmDialog(
            title = "Удалить все категории?",
            onConfirm = {
                subViewModel.removeAllCategory()
                showClearCategoryDialog = false
            },
            onDismiss = { showClearCategoryDialog = false }
        )
    }

    if (showClearPaymentDialog) {
        ConfirmDialog(
            title = "Удалить все способы оплаты?",
            onConfirm = {
                subViewModel.removeAllPaymentMethod()
                showClearPaymentDialog = false
            },
            onDismiss = { showClearPaymentDialog = false }
        )
    }

    if (showClearSubscriptionDialog) {
        ConfirmDialog(
            title = "Удалить все подписки?",
            onConfirm = {
                subViewModel.removeAllSubscription()
                showClearSubscriptionDialog = false
            },
            onDismiss = { showClearSubscriptionDialog = false }
        )
    }

    if (showClearAllDialog) {
        ConfirmDialog(
            title = "Удалить все данные?",
            onConfirm = {
                subViewModel.clearAllData()
                showClearAllDialog = false
            },
            onDismiss = { showClearAllDialog = false }
        )
    }

    if (showCreateFamilyDialog) {
        CreateFamilyDialog(
            value = viewModel.familyName,
            onValueChange = { newValue ->
                viewModel.updateFamilyName(newValue)
            },
            countFamily = families.count() + 1,
            onConfirm = {
                viewModel.createFamily()
                showCreateFamilyDialog = false
            },
            onDismiss = {
                showCreateFamilyDialog = false
                viewModel.updateFamilyName("")
            }
        )
    }

    if (showAddFamilyMemberDialog) {
        AddFamilyMemberDialog(
            value = familyMemberNumber,
            onValueChange = { newValue ->
                familyMemberNumber = newValue.filter { it.isDigit() }.take(10)
            },
            onConfirm = {},
            onDismiss = { showAddFamilyMemberDialog = false }
        )
    }
}

@Composable
fun UserProfileCard(image: String?, name: String, phoneNumber: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdaptiveIcon(
                    imageSrc = image ?: R.drawable.m0nesy_featured_image.toImageResourceString(),
                    containerShape = CircleShape,
                    containerSize = 56.dp,
                    iconSize = 56.dp,
                )
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (name != " ") name else "Без имени",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AuthCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onClick() },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Войти в аккаунт",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }

                Text(
                    text = "Получите доступ к вашему списку подписок на любом устройстве.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun FamilyCard(
    families: List<FamilyEntity>,
    familyMembers: List<FamilyMemberEntity>,
    owner: String,
    modifier: Modifier = Modifier,
    onAddMemberClick: () -> Unit,
    onCreateFamilyClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Семья",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (families.isNotEmpty()) {
            families.take(3).forEach { familyEntity ->
                val members = familyMembers.filter { it.familyId == familyEntity.id }
                val role = members.first { it.user == owner }.role
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row {
                            Text(
                                text = families.first().name,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)

                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            members.forEach { user ->
                                FamilyItem(
                                    user = user,
                                    onClick = { onAddMemberClick() }
                                )
                            }

                            for (i in members.size until 5) {
                                FamilyItem(
                                    onClick = { onAddMemberClick() }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = if (role == "owner") "Управление" else "Смотреть",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
        else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onCreateFamilyClick() },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Создать семью",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = "Добавляйте родных и следите за их подписками в вашей семейной группе",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FamilyCardPreview() {
    BarbershopTheme {
        FamilyCard(
            emptyList(),
            emptyList(),
            owner = "",
            onAddMemberClick = {},
            onCreateFamilyClick = {}
        )
    }
}

@Composable
fun FamilyItem(
    modifier: Modifier = Modifier,
    user: FamilyMemberEntity? = null,
    onClick: () -> Unit
) {
    val borderColor = if (user != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    Surface(
        modifier = modifier
            .size(48.dp),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
        onClick = { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (user != null){
                AdaptiveIcon(
                    imageSrc = user.avatarUrl ?: R.drawable.m0nesy_featured_image.toImageResourceString(),
                    containerShape = CircleShape,
                    containerSize = 48.dp,
                    iconSize = 48.dp,
                )
            }
            else{
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FamilyItemPreview() {
    BarbershopTheme {
        FamilyItem(
            onClick = {}
        )
    }
}

@Composable
fun DataCard(
    onClickClearAllCategory: () -> Unit,
    onClickClearAllSubscription: () -> Unit,
    onClickClearAllPaymentMethod: () -> Unit,
    onClickClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Данные",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = { onClickClearAllCategory() },
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Удалить категории",
                color = Color.Red.copy(0.5f)
            )
        }
        HorizontalDivider()
        Button(
            onClick = { onClickClearAllPaymentMethod() },
            shape = RoundedCornerShape(5),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Удалить платежные методы",
                color = Color.Red.copy(0.5f)
            )
        }
        HorizontalDivider()
        Button(
            onClick = { onClickClearAllSubscription() },
            shape = RoundedCornerShape(5),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Удалить подписки",
                color = Color.Red.copy(0.5f)
            )
        }
        HorizontalDivider()
        Button(
            onClick = {onClickClearAllData() },
            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Удалить все данные",
                color = Color.Red.copy(0.5f)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DataCardPreview() {
    BarbershopTheme {
        DataCard(onClickClearAllCategory = {}, onClickClearAllPaymentMethod = {}, onClickClearAllSubscription = {}, onClickClearAllData = {})
    }
}

@Composable
fun SettingsCard(
    onClickBaseCurrency: () -> Unit,
    basicCurrency: String,
    onClickTimeNotification: () -> Unit,
    timeNotification: String,
    onClickCategory: () -> Unit,
    onClickPaymentMethod: () -> Unit,
    onClickTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Конфигурация",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClickCategory() }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Категории",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider()
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClickPaymentMethod() }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Платежные методы",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider()
        TimeNotification(
            onClickTimeNotification = { onClickTimeNotification() },
            timeNotification = timeNotification
        )
        HorizontalDivider()
        TestNotificationButton(
            onClickTestNotification
        )
        HorizontalDivider()
        BaseCurrency(
            onClickBaseCurrency = { onClickBaseCurrency() },
            basicCurrency = basicCurrency
        )
    }
}

@Composable
fun TimeNotification(
    onClickTimeNotification: () -> Unit,
    timeNotification: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickTimeNotification() }

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Время уведомлений",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = timeNotification,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}

@Composable
fun TestNotificationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)

    val channelId = "test_channel"
    val channelName = "Test Notifications"

    LaunchedEffect(Unit) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager?.createNotificationChannel(channel)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val notification = NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Тестовое уведомление")
                        .setContentText("Это пример уведомления")
                        .setSmallIcon(R.mipmap.ic_launcher_adaptive_fore)
                        .setAutoCancel(true)
                        .build()

                    notificationManager?.notify(1001, notification)
                } else {
                    Toast.makeText(
                        context,
                        "Разрешение на уведомления не получено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Отправить тестовое уведомление",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}

@Composable
fun BaseCurrency(
    onClickBaseCurrency: () -> Unit,
    basicCurrency: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickBaseCurrency() }

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Валюта по умолчанию",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = basicCurrency,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text("Вы уверены, что хотите продолжить? Это действие необратимо.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun CreateFamilyDialog(
    value: String,
    onValueChange: (String) -> Unit,
    countFamily: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                modifier = modifier,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Создание семьи",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Придумайте наименование для семьи",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(16.dp))
                    TextInputField(
                        value = value.ifEmpty { "" },
                        onValueChange = { input ->
                            onValueChange(input)
                        },
                        placeholder = "По умолчанию (Семья №$countFamily)",
                        maxLength = 30,
                        showCounter = true,
                        imeAction = ImeAction.None,
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onDismiss() },
                        ) {
                            Text(
                                text = "Отмена",
                            )
                        }
                        TextButton(
                            onClick = { onConfirm() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Создать"
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AddFamilyMemberDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                modifier = modifier,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Приглашение в семью",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Введите номер телефона зарегистрированного пользователя, ему будет отправлено приглашение",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(16.dp))
                    TextInputField(
                        value = value.ifEmpty { "" },
                        onValueChange = { input ->
                            val numbersOnly = input.filter { it.isDigit() }
                            onValueChange(numbersOnly)
                                        },
                        leadingIcon = { Text("+7") },
                        imeAction = ImeAction.None,
                        singleLine = true,
                        visualTransformation = PhoneNumberMaskTransformation()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onDismiss() },
                        ) {
                            Text(
                                text = "Отмена",
                            )
                        }
                        TextButton(
                            onClick = { onConfirm() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Отправить"
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CurrencyPreview() {
    BarbershopTheme {
        BaseCurrency({}, "RUB")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserProfileCardPreview() {
    BarbershopTheme {
        UserProfileCard("","Имя Фамилия", "+79519977990")
    }
}