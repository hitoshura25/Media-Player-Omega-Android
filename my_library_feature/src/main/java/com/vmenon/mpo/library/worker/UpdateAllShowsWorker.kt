package com.vmenon.mpo.library.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.vmenon.mpo.core.work.BaseWorker
import com.vmenon.mpo.library.di.dagger.toLibraryComponent
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import javax.inject.Inject

class UpdateAllShowsWorker(
    context: Context,
    workerParams: WorkerParameters
) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var interactors: MyLibraryInteractors

    override suspend fun doMyWork(): Result {
        applicationContext.toLibraryComponent().inject(this)
        interactors.updateAllShows()
        return Result.success()
    }
}