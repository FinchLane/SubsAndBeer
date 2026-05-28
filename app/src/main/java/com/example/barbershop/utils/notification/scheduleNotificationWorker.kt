package com.example.barbershop.utils.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.Subscription
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun scheduleNotificationWorker(
    context: Context,
    subscription: Subscription,
    notification: Notification,
    time: LocalTime
) {
    val notificationDate = subscription.nextPaymentDate.minusDays(notification.days.toLong())
    val now = LocalDateTime.now()
    val scheduleDateTime = LocalDateTime.of(notificationDate, time)

    if (scheduleDateTime.isBefore(now)) return

    val delay = Duration.between(now, scheduleDateTime).toMillis()

    val data = workDataOf(
        "title" to "Подписка: ${subscription.name}",
        "message" to "Скоро спишется ${subscription.amount} ${subscription.currency}"
    )

    val workName = "notif_${subscription.id}_${notification.id}"

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        workName,
        ExistingWorkPolicy.REPLACE,
        request
    )
}