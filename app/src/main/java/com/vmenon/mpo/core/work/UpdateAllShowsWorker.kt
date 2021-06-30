package com.vmenon.mpo.core.work

import android.content.Context
import com.vmenon.mpo.core.usecases.Interactors
import javax.inject.Inject

class UpdateAllShowsWorker(
    context: Context,
    workerParams: androidx.work.WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: Interactors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)
        interactors.updateAllShows()
        return Result.success()
    }
}