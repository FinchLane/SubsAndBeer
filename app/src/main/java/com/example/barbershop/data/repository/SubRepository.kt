package com.example.barbershop.data.repository

import com.example.barbershop.data.database.subscription.CategoryDao
import com.example.barbershop.data.database.subscription.CurrencyDao
import com.example.barbershop.data.database.subscription.NotificationDao
import com.example.barbershop.data.database.subscription.PaymentMethodDao
import com.example.barbershop.data.database.subscription.SubscriptionDao
import com.example.barbershop.data.database.subscription.SubscriptionNotificationDao
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.model.subscription.SubscriptionNotification
import com.example.barbershop.data.model.subscription.SubscriptionWithNotifications
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.network.response.NotificationResponse
import com.example.barbershop.data.network.response.SubscriptionDataResponse
import com.example.barbershop.data.network.response.SyncResponse
import com.example.barbershop.data.network.response.TemplatesResponse
import com.example.barbershop.data.network.response.TokenRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class SubRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val subscriptionDao: SubscriptionDao,
    private val notificationDao: NotificationDao,
    private val subscriptionNotificationDao: SubscriptionNotificationDao,
    private val currencyDao: CurrencyDao,
    private val apiService: ApiService
) {
    suspend fun saveCategory(category: Category){
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(id: String){
        categoryDao.deleteCategory(id)
    }

    suspend fun deleteAllCategory(){
        categoryDao.deleteAllCategory()
    }

    suspend fun getCategory(id: String): Category? {
        return  categoryDao.getCategory(id)
    }

    suspend fun getCategories():List<Category> {
        return categoryDao.getAllCategory()
    }

    suspend fun savePaymentMethod(paymentMethod: PaymentMethod){
        paymentMethodDao.insertPaymentMethod(paymentMethod)
    }

    suspend fun deletePaymentMethod(id: String){
        paymentMethodDao.deletePaymentMethod(id)
    }

    suspend fun deleteAllPaymentMethods(){
        paymentMethodDao.deleteAllPaymentMethods()
    }

    suspend fun getPaymentMethod(id: String): PaymentMethod?{
        return paymentMethodDao.getPaymentMethod(id)
    }

    suspend fun getPaymentMethods(): List<PaymentMethod>{
        return paymentMethodDao.getAllPaymentMethods()
    }

    suspend fun saveSubscription(subscription: Subscription): Subscription {
        subscriptionDao.insertSubscription(subscription)
        return subscription
    }

    suspend fun deleteSubscription(id: String){
        subscriptionDao.deleteSubscription(id)
    }

    suspend fun deleteAllSubscription(){
        subscriptionDao.deleteAllSubscription()
    }

    suspend fun getSubscription(id: String): Subscription?{
        return subscriptionDao.getSubscription(id)
    }

    suspend fun getSubscriptions(): List<Subscription>?{
        return subscriptionDao.getAllSubscription()
    }

    suspend fun saveNotifications(notification: List<Notification>){
        return notificationDao.insertNotifications(notification)
    }

    suspend fun saveSubscriptionNotifications(subscriptionId: String, notificationIds: List<Int>){
        val subscriptionNotifications = notificationIds.map {
            SubscriptionNotification(subscriptionId, it)
        }
        subscriptionNotificationDao.insertSubscriptionNotifications(subscriptionNotifications)
    }

    suspend fun getAllWithNotifications(): List<SubscriptionWithNotifications> {
        return notificationDao.getAllWithNotifications()
    }

    suspend fun getSubscriptionWithNotifications(id: String): SubscriptionWithNotifications? {
        return subscriptionDao.getSubscriptionWithNotificationsById(id)
    }

    suspend fun getAllSubscriptionsWithNotifications(): List<SubscriptionWithNotifications> {
        return subscriptionDao.getAllSubscriptionsWithNotifications()
    }

    suspend fun getAllNotifications(): List<Notification> {
        return notificationDao.getAllNotifications()
    }

    suspend fun saveCurrencies(currencies: List<Currency>) {
        currencyDao.insertCurrencies(currencies)
    }

    suspend fun saveExchangeRates(rates: List<ExchangeRate>) {
        currencyDao.insertExchangeRates(rates)
    }

    suspend fun getCurrencies(): List<Currency> {
        return currencyDao.getCurrencies()
    }

    suspend fun getExchangeRate(from: String, to: String): ExchangeRate? {
        return currencyDao.getExchangeRate(from, to)
    }

    /** Синхронизация с сервером */

    suspend fun getPendingSubscriptions(): List<Subscription> {
        return subscriptionDao.getPendingSubscriptions()
    }

    suspend fun getDeletedSubscriptions(): List<Subscription> {
        return subscriptionDao.getDeletedSubscription()
    }

    suspend fun updateSubscriptionSyncStatus(id: String, status: String) {
        subscriptionDao.updateSubscriptionSyncStatus(id, status)
    }

    suspend fun clearSubscriptionTime() {
        subscriptionDao.clearSubscriptionTime()
    }

    suspend fun getPendingCategories(): List<Category> {
        return categoryDao.getPendingCategories()
    }

    suspend fun getDeletedCategories(): List<Category> {
        return categoryDao.getDeletedCategory()
    }

    suspend fun updateCategorySyncStatus(id: String, status: String) {
        categoryDao.updateCategorySyncStatus(id, status)
    }

    suspend fun clearCategoryTime() {
        categoryDao.clearCategoryTime()
    }

    suspend fun getPendingPaymentMethods(): List<PaymentMethod> {
        return paymentMethodDao.getPendingPaymentMethods()
    }

    suspend fun getDeletedPaymentMethods(): List<PaymentMethod> {
        return paymentMethodDao.getDeletedPaymentMethods()
    }

    suspend fun updatePaymentMethodSyncStatus(id: String, status: String) {
        paymentMethodDao.updatePaymentMethodSyncStatus(id, status)
    }

    suspend fun clearPaymentMethodTime() {
        paymentMethodDao.clearPaymentMethodTime()
    }

    suspend fun updateSubscriptionSyncStatusAndTime(id: String, status: String, timestamp: Long){
        subscriptionDao.updateSubscriptionSyncStatusAndTime(id, status, timestamp)
    }

    suspend fun updateCategorySyncStatusAndTime(id: String, status: String, timestamp: Long){
        categoryDao.updateCategorySyncStatusAndTime(id, status, timestamp)
    }

    suspend fun updatePaymentMethodSyncStatusAndTime(id: String, status: String, timestamp: Long){
        paymentMethodDao.updatePaymentMethodSyncStatusAndTime(id, status, timestamp)
    }

//    suspend fun getPendingSubscriptionNotification(): List<SubscriptionNotification> {
//        return subscriptionNotificationDao.getPendingSubscriptionNotification()
//    }
//
//    suspend fun updateSubscriptionNotificationSyncStatus(subscriptionId: Int, notificationId: Int, status: String) {
//        subscriptionNotificationDao.updateSyncStatusSubscriptionNotification(subscriptionId, notificationId, status)
//    }

    suspend fun getLastServerSyncTime(): Long {
        return subscriptionDao.getLastServerSyncTime() ?: 0L
    }

    /** Запросы на сервер */

    suspend fun getSyncData(syncTime: Long): Response<SubscriptionDataResponse> {
        return apiService.getSyncData(syncTime)
    }

    suspend fun addSubscriptionServer(subscription: Subscription): Response<SyncResponse> {
        return apiService.addSubscription(subscription)
    }

    suspend fun updateSubscriptionServer(subscription: Subscription): Response<SyncResponse> {
        return apiService.updateSubscription(subscription)
    }

    suspend fun deleteSubscriptionServer(id: String): Response<SyncResponse> {
        return apiService.deleteSubscription(id)
    }

    suspend fun addCategoryServer(category: Category): Response<SyncResponse> {
        return apiService.addCategory(category)
    }

    suspend fun deleteCategoryServer(id: String) : Response<SyncResponse> {
        return apiService.deleteCategory(id)
    }

    suspend fun addPaymentMethodServer(paymentMethod: PaymentMethod): Response<SyncResponse> {
        return apiService.addPaymentMethod(paymentMethod)
    }

    suspend fun deletePaymentMethodServer(id: String) : Response<SyncResponse> {
        return apiService.deletePaymentMethod(id)
    }

    suspend fun getTemplates(): Response<TemplatesResponse> {
        return apiService.getTemplates()
    }

    suspend fun updateNotificationToken(token: TokenRequest): Response<NotificationResponse> {
        return apiService.updateNotificationToken(token)
    }

    fun getSubscriptionsFlow(): Flow<List<Subscription>> =
        subscriptionDao.getAllSubscriptionFlow()

    fun getCategoriesFlow(): Flow<List<Category>> =
        categoryDao.getAllCategoriesFlow()

    fun getPaymentMethodsFlow(): Flow<List<PaymentMethod>> =
        paymentMethodDao.getAllPaymentMethodsFlow()
}