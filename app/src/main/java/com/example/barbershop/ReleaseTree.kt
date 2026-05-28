package com.example.barbershop

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree: Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return

        FirebaseCrashlytics.getInstance().log(message)
        t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
    }
}