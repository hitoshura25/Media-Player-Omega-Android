package com.vmenon.mpo.downloads.worker

import android.content.Context
import android.util.Log
import com.vmenon.mpo.core.work.BaseWorker
import com.vmenon.mpo.downloads.usecases.NotifyDownloadCompleted
import javax.inject.Inject

class DownloadCompleteWorker(
    context: Context,
    workerParams: androidx.work.WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var useCase: NotifyDownloadCompleted

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        val downloadManagerId = inputData.getLong(INPUT_DOWNLOAD_ID, -1)
        Log.d(javaClass.name, "downloadManagerId: $downloadManagerId")
        if (downloadManagerId != -1L) {
            useCase(downloadManagerId)
        }

        return Result.success()
    }

    companion object {
        const val INPUT_DOWNLOAD_ID = "downloadId"
    }
}