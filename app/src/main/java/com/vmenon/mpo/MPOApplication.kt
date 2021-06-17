package com.vmenon.mpo

import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.di.AppComponentProvider
import com.vmenon.mpo.di.AppModule
import com.vmenon.mpo.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

class MPOApplication : MultiDexApplication(), AppComponentProvider {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .thirdPartyIntegratorModule(ThirdPartyIntegratorModule())
            .build()

        appComponent.thirdPartyIntegrator().initialize(this)

        /*WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Update",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<com.vmenon.mpo.my_library.worker.UpdateAllShowsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RetryDownloads",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<com.vmenon.mpo.downloads.worker.RetryDownloadWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )*/
    }

    override fun appComponent(): AppComponent = appComponent
}
