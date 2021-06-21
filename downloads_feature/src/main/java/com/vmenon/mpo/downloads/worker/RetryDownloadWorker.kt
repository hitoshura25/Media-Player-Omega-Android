package com.vmenon.mpo.downloads.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.core.work.BaseWorker
import com.vmenon.mpo.downloads.di.dagger.toDownloadsComponent
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import javax.inject.Inject

class RetryDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: DownloadsInteractors

    override suspend fun doMyWork(): Result {
        applicationContext.toDownloadsComponent().inject(this)
        interactors.retryDownloads()
        return Result.success()
    }
}