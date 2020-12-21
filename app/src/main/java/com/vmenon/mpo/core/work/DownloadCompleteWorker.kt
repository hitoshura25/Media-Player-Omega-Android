package com.vmenon.mpo.core.work

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import javax.inject.Inject

class DownloadCompleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var downloadsInteractors: DownloadsInteractors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        val downloadManagerId = inputData.getLong(INPUT_DOWNLOAD_ID, -1)
        Log.d(javaClass.name, "downloadManagerId: $downloadManagerId")
        if (downloadManagerId != -1L) {
            downloadsInteractors.notifyDownloadCompleted(downloadManagerId)
        }

        return Result.success()
    }

    companion object {
        const val INPUT_DOWNLOAD_ID = "downloadId"
    }
}