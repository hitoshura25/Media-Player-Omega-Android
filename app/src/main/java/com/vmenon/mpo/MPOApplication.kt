package com.vmenon.mpo

import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.common.framework.di.dagger.DaggerCommonFrameworkComponent
import com.vmenon.mpo.di.*
import com.vmenon.mpo.navigation.framework.di.dagger.DaggerNavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.DaggerPersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent

class MPOApplication : SplitCompatApplication(),
    CommonFrameworkComponentProvider, SystemFrameworkComponentProvider, AuthComponentProvider {
    lateinit var appComponent: AppComponent
    private lateinit var systemFrameworkComponent: SystemFrameworkComponent
    lateinit var commonFrameworkComponent: CommonFrameworkComponent
    private lateinit var authComponent: AuthComponent
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        SplitCompat.install(this)
    }

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

        val navigationFrameworkComponent = DaggerNavigationFrameworkComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .hostFragmentId(R.id.nav_host_fragment)
            .build()

        commonFrameworkComponent = DaggerCommonFrameworkComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .authComponent(authComponent)
            .persistenceComponent(persistenceComponent)
            .navigationFrameworkComponent(navigationFrameworkComponent)
            .build()

        appComponent = DaggerAppComponent.builder()
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

    override fun commonFrameworkComponent(): CommonFrameworkComponent = commonFrameworkComponent
    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
    override fun authComponent(): AuthComponent = authComponent
}
