package com.vmenon.mpo.core.work

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.di.AppComponentProvider

abstract class BaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @VisibleForTesting
    internal lateinit var appComponent: AppComponent

    final override suspend fun doWork(): Result {
        appComponent = (applicationContext as AppComponentProvider).appComponent()
        return doMyWork()
    }

    abstract suspend fun doMyWork(): Result
}