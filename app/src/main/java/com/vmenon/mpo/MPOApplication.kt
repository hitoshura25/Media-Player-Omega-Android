package com.vmenon.mpo

import androidx.multidex.MultiDexApplication
import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.common.framework.di.dagger.DaggerCommonFrameworkComponent
import com.vmenon.mpo.di.*
import com.vmenon.mpo.persistence.di.dagger.DaggerPersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent

class MPOApplication : MultiDexApplication(), AppComponentProvider,
    CommonFrameworkComponentProvider, SystemFrameworkComponentProvider, AuthComponentProvider {
    lateinit var appComponent: AppComponent
    lateinit var systemFrameworkComponent: SystemFrameworkComponent
    lateinit var commonFrameworkComponent: CommonFrameworkComponent
    lateinit var authComponent: AuthComponent
    override fun onCreate() {
        super.onCreate()

        systemFrameworkComponent = DaggerSystemFrameworkComponent.builder()
            .application(this)
            .build()

        authComponent = DaggerAuthComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

        val persistenceComponent = DaggerPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()

        commonFrameworkComponent = DaggerCommonFrameworkComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .authComponent(authComponent)
            .persistenceComponent(persistenceComponent)
            .build()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule())
            .thirdPartyIntegratorModule(ThirdPartyIntegratorModule())
            .commonFrameworkComponent(commonFrameworkComponent)
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
    override fun commonFrameworkComponent(): CommonFrameworkComponent = commonFrameworkComponent
    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
    override fun authComponent(): AuthComponent = authComponent
}
