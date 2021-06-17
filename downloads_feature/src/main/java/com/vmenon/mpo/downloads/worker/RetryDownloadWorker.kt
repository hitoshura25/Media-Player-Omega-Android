package com.vmenon.mpo.downloads.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.core.work.BaseWorker
import com.vmenon.mpo.downloads.usecases.RetryDownloads
import javax.inject.Inject

class RetryDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var useCase: RetryDownloads

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        useCase()
        return Result.success()
    }
}