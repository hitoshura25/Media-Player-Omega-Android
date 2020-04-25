package com.vmenon.mpo

import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vmenon.mpo.api.di.dagger.DaggerApiComponent
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.di.AppModule
import com.vmenon.mpo.di.DaggerAppComponent
import com.vmenon.mpo.player.di.dagger.DaggerPlayerComponent
import com.vmenon.mpo.repository.di.dagger.DaggerRepositoryComponent
import java.util.concurrent.TimeUnit

class MPOApplication : MultiDexApplication() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .repositoryComponent(DaggerRepositoryComponent.builder()
                .application(this)
                .apiComponent(DaggerApiComponent.builder().build())
                .build())
            .playerComponent(DaggerPlayerComponent.builder().application(this).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Update",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<UpdateAllShowsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )
    }
}
