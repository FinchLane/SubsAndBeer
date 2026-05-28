package com.example.barbershop.utils.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.barbershop.AppSettings
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.repository.SubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class UploadWorker (
    context: Context,
    params: WorkerParameters,
    private val subRepository: SubRepository,
    private val apiService: ApiService,
    private val appSettings: AppSettings
) : CoroutineWorker(context, params) {

    var maxTimestamp = 0L

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Timber.Forest.tag("UploadWorker").d("UploadWorker запущен")
            val pendingCategories = subRepository.getPendingCategories()
            for (category in pendingCategories) {
                val response = when (category.operationType) {
                    "create" -> apiService.addCategory(category)
                    "update" -> apiService.updateCategory(category)
                    "delete" -> apiService.deleteCategory(category.id)
                    else -> throw IllegalArgumentException("Unknown operation type")
                }
                if (response.isSuccessful) {
                    subRepository.updateCategorySyncStatusAndTime(
                        category.id,
                        "synced",
                        response.body()?.timestamp ?: 0L
                    )
                    response.body()?.timestamp?.let {
                        maxTimestamp = maxOf(maxTimestamp, it)
                    }
                } else {
                    Timber.Forest.tag("UploadWorker")
                        .e("API error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            val pendingPaymentMethods = subRepository.getPendingPaymentMethods()
            for (paymentMethod in pendingPaymentMethods) {
                val response = when (paymentMethod.operationType) {
                    "create" -> apiService.addPaymentMethod(paymentMethod)
                    "update" -> apiService.updatePaymentMethod(paymentMethod)
                    "delete" -> apiService.deletePaymentMethod(paymentMethod.id)
                    else -> throw IllegalArgumentException("Unknown operation type")
                }
                if (response.isSuccessful) {
                    subRepository.updatePaymentMethodSyncStatusAndTime(
                        paymentMethod.id,
                        "synced",
                        response.body()?.timestamp ?: 0L
                    )
                    response.body()?.timestamp?.let {
                        maxTimestamp = maxOf(maxTimestamp, it)
                    }
                } else {
                    Timber.Forest.tag("UploadWorker")
                        .e("API error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            val pendingSubscriptions = subRepository.getPendingSubscriptions()
            for (subscription in pendingSubscriptions) {
                val response = when (subscription.operationType) {
                    "create" -> apiService.addSubscription(subscription)
                    "update" -> apiService.updateSubscription(subscription)
                    "delete" -> apiService.deleteSubscription(subscription.id)
                    else -> throw IllegalArgumentException("Unknown operation type")
                }
                if (response.isSuccessful) {
                    subRepository.updateSubscriptionSyncStatusAndTime(
                        subscription.id,
                        "synced",
                        response.body()?.timestamp ?: 0L
                    )
                    response.body()?.timestamp?.let {
                        maxTimestamp = maxOf(maxTimestamp, it)
                    }
                } else {
                    Timber.Forest.tag("UploadWorker")
                        .e("API error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            subRepository.getDeletedSubscriptions().forEach { subscription ->
                subRepository.deleteSubscription(subscription.id)
            }
            subRepository.getDeletedCategories().forEach { category ->
                subRepository.deleteCategory(category.id)
            }
            subRepository.getDeletedPaymentMethods().forEach { paymentMethod ->
                subRepository.deletePaymentMethod(paymentMethod.id)
            }

            appSettings.setLastSyncTime(maxTimestamp)
            Result.success()
        } catch (e: Exception) {
            Timber.Forest.tag("UploadWorker").e("Error syncing data: ${e.message}")
            Result.retry()
        }
    }
}