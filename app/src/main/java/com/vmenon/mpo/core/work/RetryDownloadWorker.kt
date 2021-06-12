package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import javax.inject.Inject

class RetryDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var downloadsInteractors: DownloadsInteractors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        downloadsInteractors.retryDownloads()
        return Result.success()
    }
}