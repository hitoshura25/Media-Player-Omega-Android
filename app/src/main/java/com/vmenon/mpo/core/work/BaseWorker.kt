package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.di.AppComponent

abstract class BaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    protected lateinit var appComponent: AppComponent

    final override suspend fun doWork(): Result {
        appComponent = (applicationContext as MPOApplication).appComponent
        return doMyWork()
    }

    abstract suspend fun doMyWork(): Result
}