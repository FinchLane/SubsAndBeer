package com.example.barbershop.utils.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.barbershop.AppSettings
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.repository.SubRepository
import jakarta.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val subRepository: SubRepository,
    private val apiService: ApiService,
    private val appSettings: AppSettings
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UploadWorker::class.java.name -> UploadWorker(appContext, workerParameters, subRepository, apiService, appSettings)
            DownloadWorker::class.java.name -> DownloadWorker(appContext, workerParameters, subRepository, appSettings)
            else -> null
        }
    }
}