package com.example.barbershop.utils.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.barbershop.MainActivity

class AlarmReceiver: BroadcastReceiver() {
    private val CHANNEL_ID = "channel_id"

    override fun onReceive(p0: Context, p1: Intent?) {
        sendNotification(context = p0)
    }

    private fun sendNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

    }
}