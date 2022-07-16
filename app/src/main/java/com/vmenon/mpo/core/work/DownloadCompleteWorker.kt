package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.core.usecases.Interactors
import com.vmenon.mpo.system.domain.Logger
import javax.inject.Inject

class DownloadCompleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: Interactors

    @Inject
    lateinit var logger: Logger

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        val downloadManagerId = inputData.getLong(INPUT_DOWNLOAD_ID, -1)
        logger.println("downloadManagerId: $downloadManagerId")
        if (downloadManagerId != -1L) {
            interactors.notifyDownloadCompleted(downloadManagerId)
        }

        return Result.success()
    }

    companion object {
        const val INPUT_DOWNLOAD_ID = "downloadId"
    }
}