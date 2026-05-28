package com.example.barbershop.viewmodel.subscription

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.barbershop.AppSettings
import com.example.barbershop.R
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.NotificationCheckbox
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.model.subscription.template.CategoryTemplate
import com.example.barbershop.data.model.subscription.template.Template
import com.example.barbershop.data.model.subscription.template.TemplatePlans
import com.example.barbershop.data.repository.SubRepository
import com.example.barbershop.ui.components.customComponent.toImageResourceString
import com.example.barbershop.utils.currency.fetchCurrenciesFromCBR
import com.example.barbershop.utils.notification.scheduleNotificationWorker
import com.example.barbershop.utils.sub.SubUtils.calculateNextDate
import com.example.barbershop.utils.worker.DownloadWorker
import com.example.barbershop.utils.worker.UploadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubViewModel @Inject constructor(
    private val subRepository: SubRepository,
    private val appSettings: AppSettings,
    @ApplicationContext
    private val context: Context
): ViewModel() {
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> get() = _categories

    private val _paymentMethods = mutableStateListOf<PaymentMethod>()
    val paymentMethods: List<PaymentMethod> get() = _paymentMethods

    private val _subscription = mutableStateListOf<Subscription>()
    val subscription: List<Subscription> get() = _subscription

    private val _categoriesTemplate = mutableStateListOf<CategoryTemplate>()
    private val _templates = mutableStateListOf<Template>()
    private val _plans = mutableStateListOf<TemplatePlans>()

    val categoriesTemplate: List<CategoryTemplate> get() = _categoriesTemplate
    val templates: List<Template> get() = _templates
    val plans: List<TemplatePlans> get() = _plans

    var templatePlans by mutableStateOf<List<TemplatePlans>>(emptyList())
        private set

    val baseCurrencyFlow: Flow<String> = appSettings.baseCurrencyFlow
    val notificationTimeFlow: Flow<String> = appSettings.notificationTimeFlow

    var uiState by mutableStateOf(SubUiState())
        private set

    init {
        viewModelScope.launch {
            updateCurrenciesFromCBR()
            val currencies = subRepository.getCurrencies()
            uiState = uiState.copy(availableCurrencies = currencies)
            getBasicCurrencies() // поправить
            getNotificationTime()
            getCurrencies()
            loadData()
        }
    }

    private fun loadData() {
        _categories.clear()
        _paymentMethods.clear()
        _subscription.clear()
        getCategory()
        getPaymentMethods()
        getSubscription()
    }

    fun updateState(id: Int, isChecked: Boolean){
        val updatedCheckboxes = uiState.notificationCheckboxes.map {
            if (it.id == id) it.copy(isChecked = isChecked) else it
        }
        uiState = uiState.copy(notificationCheckboxes = updatedCheckboxes)
    }

    fun updateSubscriptionId(id: String) {
        uiState = uiState.copy(id = id)
    }

    fun updateSelectedSorted(id: Int) {
        uiState = uiState.copy(selectedSorted = id)
    }

    fun updateSelectedPeriod(period: String) {
        uiState = uiState.copy(selectedPeriod = period)
    }

    fun updateSubscriptionName(input: String){
        uiState = uiState.copy(name = input)
    }

    fun updateCategoryName(input: String){
        uiState = uiState.copy(nameCategory = input)
    }

    fun updateNamePaymentMethod(input: String){
        uiState = uiState.copy(namePaymentMethod = input)
    }

    fun updateAmount(input: String){
        uiState = uiState.copy(amount = input)
    }

    fun updateInterval(input: String){
        uiState = uiState.copy(interval = input)
    }

    fun updateStartDate(date: LocalDate){
        uiState = uiState.copy(startDate = date)
    }

    fun updateSelectedCategory(categoryId: String) {
        uiState = uiState.copy(selectedCategoryId = categoryId)
    }

    fun updateSelectedPaymentMethod(id: String){
        uiState = uiState.copy(selectedPaymentMethodId = id)
    }

    fun updateSelectedPlan(index: Int) {
        uiState = uiState.copy(selectedPlan = index)
    }

    fun updateTemplatePlans(plans: List<TemplatePlans>) {
        templatePlans = plans
    }

    fun resetSelectedPlan() {
        uiState = uiState.copy(selectedPlan = 0)
    }

    fun updateCancelURL(url: String) {
        uiState = uiState.copy(cancelURL = url)
    }

    fun openIconBottomSheet() {
        uiState = uiState.copy(showIconBottomSheet = true)
    }

    fun closeIconBottomSheet() {
        uiState = uiState.copy(showIconBottomSheet = false)
    }

    fun openTemplatePLanBottomSheet() {
        uiState = uiState.copy(showTemplatePlanBottomSheet = true)
    }

    fun closeTemplatePLanBottomSheet() {
        uiState = uiState.copy(showTemplatePlanBottomSheet = false)
    }

    fun openCategory(){
        uiState = if (categories.isEmpty()){
            uiState.copy(showDialogCategory = true)
        } else {
            uiState.copy(showCategoryBottomSheet = true)
        }
    }

    fun openCategoryDialog() {
        uiState = uiState.copy(showDialogCategory = true)
    }

    fun openPaymentMethod(){
        uiState = if (paymentMethods.isEmpty()){
            uiState.copy(showDialogPaymentMethod = true)
        } else {
            uiState.copy(showPaymentMethodBottomSheet = true)
        }
    }

    fun openPaymentMethodDialog() {
        uiState = uiState.copy(showDialogPaymentMethod = true)
    }

    fun closeCategoryBottomSheet(){
        uiState = uiState.copy(showCategoryBottomSheet = false)
    }

    fun closePaymentMethodBottomSheet(){
        uiState = uiState.copy(showPaymentMethodBottomSheet = false)
    }

    fun closeDialogCategory(){
        uiState = uiState.copy(showDialogCategory = false)
    }

    fun closeDialogPaymentMethod(){
        uiState = uiState.copy(showDialogPaymentMethod = false)
    }

    fun openNotificationBottomSheet(){
        uiState = uiState.copy(showNotificationBottomSheet = true)
    }

    fun closeNotificationBottomSheet(){
        uiState = uiState.copy(showNotificationBottomSheet = false)
    }

    fun openActionBottomSheet(){
        uiState = uiState.copy(showActionBottomSheet = true)
    }

    fun closeActionBottomSheet(){
        uiState = uiState.copy(showActionBottomSheet = false)
    }

    fun openCurrencyBottomSheet() {
        uiState = uiState.copy(showCurrencyBottomSheet = true)
    }

    fun closeCurrencyBottomSheet() {
        uiState = uiState.copy(showCurrencyBottomSheet = false)
    }

    fun openDatePicker(){
        uiState = uiState.copy(showDatePicker = true)
    }

    fun closeDatePicker(){
        uiState = uiState.copy(showDatePicker = false)
    }

    fun openTimePicker() {
        uiState = uiState.copy(showTimePicker = true)
    }

    fun closeTimePicker() {
        uiState = uiState.copy(showTimePicker = false)
    }

    fun openDeleteMessage(){
        uiState = uiState.copy(showDeleteMessage = true)
    }

    fun closeDeleteMessage(){
        uiState = uiState.copy(showDeleteMessage = false)
    }

    fun openBasicCurrencyBottomSheet(){
        uiState = uiState.copy(showBasicCurrencyBottomSheet = true)
    }

    fun closeBasicCurrencyBottomSheet(){
        uiState = uiState.copy(showBasicCurrencyBottomSheet = false)
    }

    fun updateSelectedIcon(iconRes: String) {
        uiState = uiState.copy(selectedIcon = iconRes)
    }

    fun updateSelectionColor(color: Color) {
        uiState = uiState.copy(selectionColor = color)
    }


    private fun updateErrorMessage(error: String){
        uiState = uiState.copy(errorMessage = error)
    }

    fun setBaseCurrency(currencyId: String) {
        viewModelScope.launch {
            appSettings.setBaseCurrency(currencyId)
            uiState = uiState.copy(basicCurrency = currencyId)
        }
    }

    fun setNotificationTime(time: String) {
        viewModelScope.launch {
            appSettings.setNotificationTime(time)
            uiState = uiState.copy(timeNotification = time)
        }
    }

    fun updateCurrency(currencyId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(currency = currencyId)
            val rate = subRepository.getExchangeRate("RUB", currencyId)?.rate ?: getDefaultRate(currencyId)
            uiState = uiState.copy(customRate = if (uiState.useCustomRate) uiState.customRate else rate.toString())
        }
    }

    fun updateCurrenciesFromCBR() {
        viewModelScope.launch {
            try {
                val currenciesAndRates = fetchCurrenciesFromCBR()
                subRepository.saveCurrencies(currenciesAndRates.map { it.first })
                subRepository.saveExchangeRates(currenciesAndRates.map { it.second })
                Timber.tag("SubViewModel").d("Currencies updated from CBR")
                uiState = uiState.copy(availableCurrencies = subRepository.getCurrencies())
            } catch (e: Exception) {
                updateErrorMessage("Ошибка обновления курсов: ${e.message}")
                Timber.tag("SubViewModel").e(e, "Error updating currencies: ${e.message}")
            }
        }
    }

    fun selectCurrency(currencyId: String) {
        uiState = uiState.copy(selectedCurrency = currencyId, currency = currencyId)
        updateRateForCurrency(currencyId)
        closeCurrencyBottomSheet()
    }

    fun updateCustomRate(rate: String) {
        val validRate = rate.filter { it.isDigit() || it == '.' }
        uiState = uiState.copy(
            customRate = validRate,
            useCustomRate = validRate.isNotEmpty() && validRate.toDoubleOrNull()?.let { it > 0 } == true
        )
    }

    fun getBottomSheetCurrencies(): List<Currency> {
        val usedCurrencies = subscription.map { it.currency }.toSet()
        val uniqueCurrencies = (uiState.currencyBottomSheet + usedCurrencies).distinct()
        return uiState.availableCurrencies.filter { it.id in uniqueCurrencies }
    }

    fun updateCurrencyBottomSheet(currencyId: String) {
        if (currencyId !in uiState.currencyBottomSheet) {
            uiState = uiState.copy(
                currencyBottomSheet = uiState.currencyBottomSheet + currencyId
            )
        }
    }

    private fun updateRateForCurrency(currencyId: String) {
        viewModelScope.launch {
            val rate = subRepository.getExchangeRate("RUB", currencyId)?.rate
                ?: getDefaultRate(currencyId)
            if (!uiState.useCustomRate) {
                uiState = uiState.copy(customRate = String.format("%.2f", rate))
            }
        }
    }

    private fun getDefaultRate(currencyId: String): Double {
        return when (currencyId) {
            "RUB" -> 1.0
            "USD" -> 90.0
            "EUR" -> 100.0
            else -> 1.0
        }
    }

    private suspend fun getBasicCurrencies() {
        uiState = uiState.copy(basicCurrency = appSettings.baseCurrencyFlow.first())
    }

    private suspend fun getNotificationTime() {
        uiState = uiState.copy(timeNotification = appSettings.notificationTimeFlow.first())
    }

    fun getUsedCurrencies(): List<Currency> {
        val usedCurrencyIds = subscription.map { it.currency }.toSet()
        return uiState.availableCurrencies.filter { it.id in usedCurrencyIds }
    }

    fun infoNotification(): String {
        val selectedCheckboxes = uiState.notificationCheckboxes.filter { it.isChecked }
        return when (selectedCheckboxes.size){
            0 -> "Выключены"
            1 -> selectedCheckboxes.first().text
            else -> "Несколько (${selectedCheckboxes.joinToString { it.text }})"
        }
    }

    fun saveCategory(
        name: String,
        syncStatus: String = "synced",
        id: String = UUID.randomUUID().toString(),
        operationType: String = "create"
    ) {
        viewModelScope.launch {
            val isAuthenticated = isUserAuthenticated()
            var newCategory = Category(
                id = id,
                name = name,
                syncStatus = if (isAuthenticated) syncStatus else "pending",
                operationType = operationType
            )

            if (isAuthenticated) {
                val response = subRepository.addCategoryServer(newCategory)
                if (!response.isSuccessful) {
                    newCategory = newCategory.copy(syncStatus = "pending")
                }
            }

            _categories.add(newCategory)
            updateSelectedCategory(categories.last().id)
            subRepository.saveCategory(newCategory)
            loadData()
            if (!isAuthenticated) syncDataOnStart()
        }
    }

    fun removeCategory(id: String) {
        val category = _categories.firstOrNull { it.id == id }
        if (category != null) {
            viewModelScope.launch {
                val isAuthenticated = isUserAuthenticated()
                val deleteCategory = category.copy(
                    syncStatus = "pending",
                    operationType = "delete"
                )

                if (isAuthenticated) {
                    val response = subRepository.deleteCategoryServer(category.id)
                    if (!response.isSuccessful) {
                        subRepository.saveCategory(deleteCategory)
                        syncDataOnStart()
                    } else {
                        subRepository.deleteCategory(id)
                    }
                } else {
                    subRepository.saveCategory(deleteCategory)
                }

                _categories.removeAll { it.id == id }
                if (!isAuthenticated) syncDataOnStart()
            }
        }
    }

    fun removeAllCategory(){
        viewModelScope.launch {
            subRepository.deleteAllCategory()
            _categories.clear()
        }
    }

    fun getCategory(){
        viewModelScope.launch {
            _categories.clear()
            _categories.addAll(subRepository.getCategories().filter { it.operationType != "delete" })
        }
    }

    fun savePaymentMethod(
        name: String,
        id: String = UUID.randomUUID().toString(),
        syncStatus: String = "synced",
        operationType: String = "create"
    ) {
        viewModelScope.launch {
            val isAuthenticated = isUserAuthenticated()
            var newPaymentMethod = PaymentMethod(
                id = id,
                name = name,
                syncStatus = if (isAuthenticated) syncStatus else "pending",
                operationType = operationType
            )
            if (isAuthenticated) {
                val response = subRepository.addPaymentMethodServer(newPaymentMethod)
                if (!response.isSuccessful) {
                    newPaymentMethod = newPaymentMethod.copy(syncStatus = "pending")
                }
            }

            _paymentMethods.add(newPaymentMethod)
            updateSelectedPaymentMethod(paymentMethods.last().id)
            subRepository.savePaymentMethod(newPaymentMethod)
            loadData()
            if (!isAuthenticated) syncDataOnStart()
        }
    }

    fun removePaymentMethod(id: String){
        //_paymentMethods.removeAll {it.id == id}
        val paymentMethod = _paymentMethods.firstOrNull { it.id == id }
        if (paymentMethod != null){
            viewModelScope.launch {
                val isAuthenticated = isUserAuthenticated()
                val deletedPaymentMethod = paymentMethod.copy(
                    syncStatus = "pending",
                    operationType = "delete"
                )
                if (isAuthenticated) {
                    val response = subRepository.deletePaymentMethodServer(paymentMethod.id)
                    if (!response.isSuccessful) {
                        subRepository.savePaymentMethod(deletedPaymentMethod)
                        syncDataOnStart()
                    }
                    else {
                        subRepository.deletePaymentMethod(id)
                    }
                }
                else {
                    subRepository.savePaymentMethod(deletedPaymentMethod)
                }

                _paymentMethods.removeAll{ it.id == id }
                if (!isAuthenticated) syncDataOnStart()
            }
        }
    }

    fun removeAllPaymentMethod() {
        viewModelScope.launch {
            subRepository.deleteAllPaymentMethods()
            _paymentMethods.clear()
        }
    }

    fun getPaymentMethods(){
        viewModelScope.launch {
            _paymentMethods.clear()
            _paymentMethods.addAll(
                subRepository.getPaymentMethods().filter { it.operationType != "delete" }
            )
        }
    }

    fun getCurrencies() {
        viewModelScope.launch {
            val currencies = subRepository.getCurrencies()
            uiState = uiState.copy(availableCurrencies = currencies)
        }
    }

    fun saveSubscription(
        isArchive: Boolean = false,
        syncStatus: String = "synced"
    ) {
        val selectedCheckboxes = uiState.notificationCheckboxes.filter { it.isChecked }
        val notificationIds = selectedCheckboxes.map { it.id }
        viewModelScope.launch {
            try {
                val isAuthenticated = isUserAuthenticated()

                val dbNotifications = subRepository.getAllNotifications()
                val dbNotificationIds = dbNotifications.map { it.id }.toSet()
                val missingIds = notificationIds.filter { it !in dbNotificationIds }

                val rate = if (uiState.useCustomRate) {
                    uiState.customRate.toDoubleOrNull() ?: getDefaultRate(uiState.currency)
                } else {
                    subRepository.getExchangeRate("RUB", uiState.currency)?.rate ?: getDefaultRate(uiState.currency)
                }

                if (missingIds.isNotEmpty()) {
                    val defaultNotifications = uiState.notificationCheckboxes
                        .filter { it.id in missingIds }
                        .map { Notification(it.id, it.text, it.days) }
                    subRepository.saveNotifications(defaultNotifications)
                    Timber.tag("SubViewModel")
                        .d("Inserted missing notifications: $defaultNotifications")
                }
                var subscription = Subscription(
                    id = uiState.id ?: UUID.randomUUID().toString(),
                    name = uiState.name,
                    iconSource = uiState.selectedIcon,
                    backgroundColor = uiState.selectionColor.toArgb(),
                    amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                    currency = uiState.currency,
                    period = uiState.selectedPeriod,
                    interval = uiState.interval.toIntOrNull() ?: 1,
                    startDate = uiState.startDate,
                    nextPaymentDate = calculateNextDate(
                        interval = uiState.interval,
                        selectedPeriod = uiState.selectedPeriod,
                        startDate = uiState.startDate
                    ),
                    categoryId = if (uiState.selectedCategoryId != "-1") uiState.selectedCategoryId else null,
                    paymentMethodId = if (uiState.selectedPaymentMethodId != "-1") uiState.selectedPaymentMethodId else null,
                    cancelUrl = uiState.cancelURL,
                    isArchive = isArchive,
                    syncStatus = if (isAuthenticated) syncStatus else "pending",
                    operationType = "create"
                )
                val savedSubscription = subRepository.saveSubscription(subscription)
                if (notificationIds.isNotEmpty()) {
                    subRepository.saveSubscriptionNotifications(savedSubscription.id, notificationIds)
                }
                if (isAuthenticated) {
                    val response = subRepository.addSubscriptionServer(subscription)
                    if (!response.isSuccessful) {
                        subscription = subscription.copy(syncStatus = "pending")
                        syncDataOnStart()
                        _navigateTo.value = "home"
                        clearData()
                    }
                }
                loadData()
                if (!isAuthenticated) syncDataOnStart()
                _navigateTo.value = "home"
                clearData()
                scheduleAllNotifications()
            } catch (e: Exception) {
                updateErrorMessage("Ошибка сохранения: ${e.message}")
                Timber.tag("SubViewModel").d("Error: ${e.message}")
            }
        }
    }

    fun loadSubscription(id: String){
        viewModelScope.launch {
            val subscriptionWithNotifications = subRepository.getSubscriptionWithNotifications(id)
            subscriptionWithNotifications?.let { sub ->
                val subscription = sub.subscription
                val selectedNotificationIds = sub.notifications.map { it.id }.toSet()

                Timber.tag("SubViewModel").d("Loaded subscription: $subscription")
                Timber.tag("SubViewModel").d("Loaded notifications: ${sub.notifications}")

                val updatedCheckboxes = uiState.notificationCheckboxes.map { checkbox ->
                    checkbox.copy(isChecked = checkbox.id in selectedNotificationIds)
                }

                uiState = uiState.copy(
                    id = subscription.id,
                    name = subscription.name,
                    selectedIcon = subscription.iconSource,
                    selectionColor = Color(subscription.backgroundColor),
                    amount = subscription.amount.toString(),
                    interval = subscription.interval.toString(),
                    selectedPeriod = subscription.period,
                    startDate = subscription.startDate,
                    selectedCategoryId = subscription.categoryId ?: "-1",
                    notificationCheckboxes = updatedCheckboxes,
                    cancelURL = subscription.cancelUrl ?: "",
                    currency = subscription.currency,
                    errorMessage = ""
                )
                scheduleAllNotifications()
            } ?: run {
                updateErrorMessage("Подписка с id $id не найдена")
            }
        }
    }

    fun archiveSubscription(id: String){
        viewModelScope.launch {
            try {
                val subscription = subscription.firstOrNull{it.id == id}
                if (subscription != null){
                    val archivedSubscription = subscription.copy(isArchive = true)
                    subRepository.saveSubscription(archivedSubscription)
                    _subscription.removeIf{it.id == id}
                    _subscription.add(archivedSubscription)
                    scheduleAllNotifications()
                }
                else {
                    updateErrorMessage("Подписка с id $id не найдена")
                }
            }
            catch (e: Exception){
                updateErrorMessage("Ошибка архивирования: ${e.message}")
                Timber.tag("SubViewModel").e(e, "Error archiving subscription")
            }
        }
    }

    fun unarchiveSubscription(id: String){
        viewModelScope.launch {
            try {
                val subscription = subscription.firstOrNull{it.id == id}
                if (subscription != null){
                    val archivedSubscription = subscription.copy(isArchive = false)
                    subRepository.saveSubscription(archivedSubscription)
                    _subscription.removeIf{it.id == id}
                    _subscription.add(archivedSubscription)
                    scheduleAllNotifications()
                }
                else {
                    updateErrorMessage("Подписка с id $id не найдена")
                }
            }
            catch (e: Exception){
                updateErrorMessage("Ошибка разархивирования: ${e.message}")
                Timber.tag("SubViewModel").e(e, "Error archiving subscription")
            }
        }
    }

    fun clearData(){
        uiState = uiState.copy(
            id = null,
            name = "",
            selectedIcon = R.drawable.ico_20.toImageResourceString(),
            selectionColor = Color(0xff14a9a9).copy(0.2f),
            amount = "",
            interval = "",
            selectedPeriod = "Месяц",
            selectedPlan = 0,
            startDate = LocalDate.now(),
            selectedCategoryId = "-1",
            notificationCheckboxes = listOf(
                NotificationCheckbox(1, "за 1 день", 1, false),
                NotificationCheckbox(2, "за 2 дня", 2, false),
                NotificationCheckbox(3, "за 5 дней", 5, false)
            ),
            cancelURL = "",
            errorMessage = ""
        )
    }


    fun updateSubscription(sub: Subscription) {
        viewModelScope.launch {
            val isAuthenticated = isUserAuthenticated()

            var updatedSub = sub.copy(
                syncStatus = if (isAuthenticated) "synced" else "pending",
                operationType = "update"
            )

            if (isAuthenticated) {
                try {
                    val response = subRepository.updateSubscriptionServer(updatedSub)
                    if (!response.isSuccessful) {
                        updatedSub = updatedSub.copy(syncStatus = "pending")
                    }
                } catch (e: Exception) {
                    updatedSub = updatedSub.copy(syncStatus = "pending")
                    Timber.tag("SubViewModel").e(e, "Update subscription error: ${e.message}")
                }
            }

            subRepository.saveSubscription(updatedSub)
            _navigateTo.value = "home"

            if (updatedSub.syncStatus == "pending") {
                syncDataOnStart()
            }
            scheduleAllNotifications()
        }
    }

    fun removeSubscription(id: String){
        viewModelScope.launch {
            val isAuthenticated = isUserAuthenticated()
            val subscription = _subscription.firstOrNull {it.id == id}
            if (subscription != null) {
                val deleteSub = subscription.copy(
                    syncStatus = "pending",
                    operationType = "delete"
                )
                if (isAuthenticated) {
                    val response = subRepository.deleteSubscriptionServer(subscription.id)
                    if (!response.isSuccessful) {
                        subRepository.saveSubscription(deleteSub)
                        syncDataOnStart()
                    }
                    else {
                        subRepository.deleteSubscription(id)
                    }
                }
                else {
                    subRepository.saveSubscription(deleteSub)
                }
                _subscription.removeAll { it.id == id }
                closeDeleteMessage()
                _navigateTo.value = "home"
                if (!isAuthenticated) syncDataOnStart()
                scheduleAllNotifications()
            }
        }
    }

    fun removeAllSubscription(){
        viewModelScope.launch {
            subRepository.deleteAllSubscription()
            _subscription.clear()
            Toast.LENGTH_SHORT
        }
    }

    fun getSubscription(){
        viewModelScope.launch {
            _subscription.clear()
            _subscription.addAll(
                subRepository.getSubscriptions()?.filter { it.operationType != "delete" }
                    ?: emptyList())

        }
    }

    fun getSubscriptionsByCategory(categoryId: String): List<Subscription> {
        return _subscription.filter { it.categoryId == categoryId }
    }

    fun getSubscriptionsByPaymentMethod(paymentMethodId: String): List<Subscription> {
        return _subscription.filter { it.paymentMethodId == paymentMethodId }
    }

    fun moveCategory(fromIndex: Int, toIndex: Int) {
        if (fromIndex in _categories.indices && toIndex in _categories.indices) {
            val category = _categories[fromIndex]
            _categories.removeAt(fromIndex)
            _categories.add(toIndex, category)
        }
    }

    fun getExchangeRate(fromCurrency: String, toCurrency: String): ExchangeRate? {
        var rate: ExchangeRate? = null
        runBlocking {
            rate = subRepository.getExchangeRate(fromCurrency, toCurrency)
        }
        return rate
    }

    fun getTemplates() {
        viewModelScope.launch {
            try {
                val response = subRepository.getTemplates()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _categoriesTemplate.apply {
                            clear()
                            addAll(data.categories)
                        }
                        _templates.apply {
                            clear()
                            addAll(data.templates)
                        }
                        _plans.apply {
                            clear()
                            addAll(data.plans)
                        }
                    }
                    if (_templates.isEmpty()){
                        _navigateTo.value = "addSub"
                    }
                    else {
                        _navigateTo.value = "newSub"
                    }

                }
                else {
                    _navigateTo.value = "addSub"
                }
            }
            catch(e: Exception){
                _navigateTo.value = "addSub"
            }
        }
    }

    fun clearNavigationEvent() {
        _navigateTo.value = null
    }

    fun clearAllData() {
        viewModelScope.launch {
            subRepository.deleteAllSubscription()
            subRepository.deleteAllCategory()
            subRepository.deleteAllPaymentMethods()
            subRepository.clearCategoryTime()
            subRepository.clearPaymentMethodTime()
            subRepository.clearSubscriptionTime()
            appSettings.setLastSyncTime(0L)
            loadData()
        }
    }

    fun syncDataOnStart() {
        viewModelScope.launch {
            if (isUserAuthenticated()) {
                val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build()

                val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build()

                WorkManager.getInstance(context)
                    .beginWith(uploadRequest)
                    .then(downloadRequest)
                    .enqueue()
            }
        }
    }

    fun scheduleAllNotifications() {
        val localTime = LocalTime.parse(uiState.timeNotification)

        viewModelScope.launch(Dispatchers.IO) {
            val subscriptions = subRepository.getAllWithNotifications()
            for (s in subscriptions) {
                for (n in s.notifications) {
                    scheduleNotificationWorker(context, s.subscription, n, localTime)
                }
            }
        }
    }

    private suspend fun isUserAuthenticated(): Boolean {
        return appSettings.isAuthorizedFlow.first()
    }
}