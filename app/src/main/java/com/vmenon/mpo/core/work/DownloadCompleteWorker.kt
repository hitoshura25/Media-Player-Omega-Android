package com.vmenon.mpo.core.work

import android.content.Context
import android.util.Log
import com.vmenon.mpo.core.usecases.Interactors
import javax.inject.Inject

class DownloadCompleteWorker(
    context: Context,
    workerParams: androidx.work.WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: Interactors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        val downloadManagerId = inputData.getLong(INPUT_DOWNLOAD_ID, -1)
        Log.d(javaClass.name, "downloadManagerId: $downloadManagerId")
        if (downloadManagerId != -1L) {
            interactors.notifyDownloadCompleted(downloadManagerId)
        }

        return Result.success()
    }

    companion object {
        const val INPUT_DOWNLOAD_ID = "downloadId"
    }
}