package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.shows.ShowUpdateManager
import javax.inject.Inject

class UpdateAllShowsWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var showUpdateManager: ShowUpdateManager

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)

        showUpdateManager.updateAllShows()
        return Result.success()
    }
}