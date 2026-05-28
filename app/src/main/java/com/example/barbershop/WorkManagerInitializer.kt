package com.example.barbershop

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.barbershop.utils.worker.DownloadWorker
import com.example.barbershop.utils.worker.UploadWorker
import timber.log.Timber

class WorkManagerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.tag("WorkManagerInitializer").d("Инициализация началась")
        val appContext = context.applicationContext

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        val workManager = WorkManager.getInstance(appContext)
        workManager.beginWith(uploadRequest)
            .then(downloadRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observeForever { workInfo ->
            if (workInfo != null) {
                Timber.tag("WorkManager").d("UploadWorker state: ${workInfo.state}")
            }
        }
        workManager.getWorkInfoByIdLiveData(downloadRequest.id).observeForever { workInfo ->
            if (workInfo != null) {
                Timber.tag("WorkManager").d("DownloadWorker state: ${workInfo.state}")
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}

