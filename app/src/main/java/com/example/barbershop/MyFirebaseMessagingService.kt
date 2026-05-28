package com.example.barbershop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.barbershop.data.model.MessageEntity
import com.example.barbershop.data.network.response.TokenRequest
import com.example.barbershop.data.repository.ProfileRepository
import com.example.barbershop.data.repository.SubRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var repository: SubRepository
    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM_TOKEN").d(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.updateNotificationToken(TokenRequest(token))
                if (response.isSuccessful) {
                    Timber.tag("FCM_TOKEN").d("Токен успешно отправлен на сервер")
                }
                else {
                    Timber.tag("FCM_TOKEN").e("Ошибка при отправке токена: ${response.code()}")
                }
            }
            catch (e: Exception) {
                Timber.tag("FCM_TOKEN").e("Ошибка при отправке токена: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "Уведомление"
        val body = message.notification?.body ?: ""
        val data = message.data

        val screen = data["screen"]
        val id = data["id"]

        Timber.tag("FCM_DATA").d("Открыть экран: $screen, ID: $id")

        val analytics = Firebase.analytics
        val bundle = Bundle().apply {
            putString("screen", screen)
            putString("id", id)
        }
        analytics.logEvent("fcm_notification_received", bundle)

        CoroutineScope(Dispatchers.IO).launch {
            val messageEntity = MessageEntity(
                title = title,
                body = body,
                screen = screen,
                screenId = id
            )
            profileRepository.insertMessage(messageEntity)
        }

        sendNotification(title, body, screen, id)
    }

    private fun sendNotification(title: String?, body: String?, screen: String?, subId: String? = null) {
        val channelId = "default_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Основные уведомления",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Канал для уведомлений приложения"
        }
        notificationManager.createNotificationChannel(channel)

        val deepLinkUri = when (screen) {
            "subInfo" -> "app://dora/subs/$subId"
            else -> null
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = deepLinkUri?.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.img)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        val analytics = Firebase.analytics
        val openBundle = Bundle().apply {
            putString("screen", screen)
            putString("id", subId)
        }
        analytics.logEvent("fcm_notification_shown", openBundle)
    }
}