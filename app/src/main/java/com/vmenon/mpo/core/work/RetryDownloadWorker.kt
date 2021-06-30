package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.core.usecases.Interactors
import javax.inject.Inject

class RetryDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: Interactors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        interactors.retryDownloads()
        return Result.success()
    }
}