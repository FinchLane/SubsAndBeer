package com.example.barbershop.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat

fun readCallLog(context: Context):String? {

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    val oneMinuteAgo = System.currentTimeMillis() - 60 * 1000

    val cursor = context.contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE),
        "${CallLog.Calls.TYPE} = ?",
        arrayOf(CallLog.Calls.INCOMING_TYPE.toString()),
        "${CallLog.Calls.DATE} DESC"
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
            val timestamp = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
            if (timestamp >= oneMinuteAgo) {
                return number.takeLast(4)
            }
        }
    }
    return null
}