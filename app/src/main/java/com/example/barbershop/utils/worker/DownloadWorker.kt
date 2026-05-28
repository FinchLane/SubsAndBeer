package com.example.barbershop.utils.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.barbershop.AppSettings
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.network.response.SubscriptionDataResponse
import com.example.barbershop.data.repository.SubRepository
import com.example.barbershop.utils.sub.SubUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class DownloadWorker (
    context: Context,
    params: WorkerParameters,
    private val subRepository: SubRepository,
    private val appSettings: AppSettings,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val lastSyncTime = appSettings.lastSyncTimeFlow.first()
            val response = subRepository.getSyncData(lastSyncTime)
            if (response.isSuccessful) {
                response.body()?.let { syncData ->
                    mergeData(syncData)
                    appSettings.setLastSyncTime(syncData.timestamp)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.Forest.tag("DownloadWorker").e("Error: ${e.message}")
            Result.retry()
        }
    }

    suspend fun mergeData(syncData: SubscriptionDataResponse) {
        try {
            syncData.deletedCategories?.forEach { category ->
                subRepository.deleteCategory(category)
            }

            syncData.deletedPaymentMethods?.forEach { paymentMethod ->
                subRepository.deletePaymentMethod(paymentMethod)
            }

            syncData.deletedSubscriptions?.forEach { subscription ->
                subRepository.deleteSubscription(subscription)
            }

            syncData.categories?.forEach { serverCat ->
                val localCat = subRepository.getCategory(serverCat.id)
                if (localCat == null) {
                    val newCategory = Category(
                        id = serverCat.id,
                        name = serverCat.name,
                        syncStatus = "synced",
                        operationType = "create",
                        lastServerSyncTime = serverCat.lastServerSyncTime
                    )
                    subRepository.saveCategory(newCategory)
                }
            }

            syncData.paymentMethods?.forEach { serverPayment ->
                val localCat = subRepository.getPaymentMethod(serverPayment.id)
                if (localCat == null) {
                    val newPaymentMethod = PaymentMethod(
                        id = serverPayment.id,
                        name = serverPayment.name,
                        syncStatus = "synced",
                        operationType = "create",
                        lastServerSyncTime = serverPayment.lastServerSyncTime
                    )
                    subRepository.savePaymentMethod(newPaymentMethod)
                }
            }

            syncData.subscriptions?.forEach { serverSub ->
                val localSub = subRepository.getSubscription(serverSub.id)

                if (localSub == null) {
                    val newSubscription = Subscription(
                        id = serverSub.id,
                        name = serverSub.name,
                        iconSource = serverSub.iconSource,
                        backgroundColor = serverSub.backgroundColor,
                        amount = serverSub.amount,
                        currency = serverSub.currency,
                        interval = serverSub.interval.toInt(),
                        period = serverSub.period,
                        startDate = serverSub.startDate,
                        nextPaymentDate = SubUtils.calculateNextDate(
                            interval = serverSub.interval.toString(),
                            selectedPeriod = serverSub.period,
                            startDate = serverSub.startDate
                        ),
                        categoryId = serverSub.categoryId,
                        paymentMethodId = serverSub.paymentMethodId,
                        cancelUrl = serverSub.cancelUrl,
                        isArchive = serverSub.isArchive,
                        syncStatus = "synced",
                        operationType = "create",
                        lastServerSyncTime = serverSub.lastServerSyncTime

                    )

                    subRepository.saveSubscription(newSubscription)
                } else if (serverSub.lastServerSyncTime > localSub.lastServerSyncTime) {
                    var updatedSub = serverSub.copy(
                        syncStatus = "synced",
                        operationType = "update"
                    )
                    val response = subRepository.updateSubscriptionServer(updatedSub)
                    if (!response.isSuccessful) {
                        updatedSub = updatedSub.copy(syncStatus = "pending")
                    }
                    subRepository.saveSubscription(updatedSub)
                }
            }

        } catch (e: Exception) {
            Timber.Forest.tag("mergeData").e("Synchronization error from the client: ${e.message}")
        }
    }
}