package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import javax.inject.Inject

class UpdateAllShowsWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var myLibraryInteractors: MyLibraryInteractors

    override suspend fun doMyWork(): Result {
        appComponent.inject(this)

        myLibraryInteractors.updateAllShows()
        return Result.success()
    }
}