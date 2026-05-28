package com.example.barbershop

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.barbershop.utils.worker.CustomWorkerFactory
import com.example.barbershop.utils.worker.DownloadWorker
import com.example.barbershop.utils.worker.UploadWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApp: Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
//        Timber.plant(ReleaseTree())
        FirebaseApp.initializeApp(this)
        syncDataOnStart()
    }

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(customWorkerFactory)
            .build()


    fun syncDataOnStart() {
        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(this)
            .beginWith(uploadRequest)
            .then(downloadRequest)
            .enqueue()
    }
}