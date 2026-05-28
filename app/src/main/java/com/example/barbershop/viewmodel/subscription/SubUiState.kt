package com.example.barbershop.viewmodel.subscription

import androidx.compose.ui.graphics.Color
import com.example.barbershop.R
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.NotificationCheckbox
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import java.time.LocalDate

data class SubUiState(
    val id: String? = null,
    val name: String = "",
    val amount: String = "",
    val interval: String = "",
    val nameCategory: String = "",
    val namePaymentMethod: String = "",
    val cancelURL: String = "",
    val errorMessage: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val nextPaymentDate: LocalDate? = null,
    val showIconBottomSheet: Boolean = false,
    val showTemplatePlanBottomSheet: Boolean = false,
    val showNotificationBottomSheet: Boolean = false,
    val showCategoryBottomSheet: Boolean = false,
    val showPaymentMethodBottomSheet: Boolean = false,
    val showActionBottomSheet: Boolean = false,
    val showBasicCurrencyBottomSheet: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showDeleteMessage: Boolean = false,
    val selectedSorted: Int = 1,
    val selectedCategoryId: String = "-1",
    val selectedPaymentMethodId: String = "-1",
    val showDialogCategory: Boolean = false,
    val showDialogPaymentMethod: Boolean = false,
    val selectedIcon: String = R.drawable.ico_20.toImageResourceString(),
    val selectionColor: Color = Color(0xff14a9a9).copy(0.2f),
    val periodList: List<String> = listOf("День", "Неделя", "Месяц", "Год"),
    val selectedPeriod: String = "Месяц",
    val selectedPlan: Int = 0,
    val notificationCheckboxes: List<NotificationCheckbox> = listOf(
        NotificationCheckbox(1, "за 1 день", 1, false),
        NotificationCheckbox(2, "за 2 дня", 2, false),
        NotificationCheckbox(3, "за 5 дней", 5, false)
    ),
    val currency: String = "RUB",
    val currencyBottomSheet: List<String> = listOf("RUB", "USD", "EUR"),
    val basicCurrency: String = "",
    val timeNotification: String = "",
    val customRate: String = "",
    val useCustomRate: Boolean = false,
    val availableCurrencies: List<Currency> = emptyList(),
    val showCurrencyBottomSheet: Boolean = false,
    val selectedCurrency: String = "RUB"
)
