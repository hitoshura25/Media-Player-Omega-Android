package com.vmenon.mpo

import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.di.dagger.DaggerAuthComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponentProvider
import com.vmenon.mpo.common.framework.di.dagger.DaggerCommonFrameworkComponent
import com.vmenon.mpo.core.work.RetryDownloadWorker
import com.vmenon.mpo.di.*
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.downloads.framework.di.dagger.DaggerDownloadsFrameworkComponent
import com.vmenon.mpo.my_library.framework.di.dagger.DaggerLibraryFrameworkComponent
import com.vmenon.mpo.navigation.framework.di.dagger.DaggerNavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.DaggerPersistenceComponent
import com.vmenon.mpo.player.framework.di.dagger.DaggerPlayerFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponent
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponentProvider
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import java.util.concurrent.TimeUnit

class MPOApplication : SplitCompatApplication(),
    CommonFrameworkComponentProvider, SystemFrameworkComponentProvider, AuthComponentProvider,
    PlayerFrameworkComponentProvider {
    lateinit var appComponent: AppComponent
    lateinit var commonFrameworkComponent: CommonFrameworkComponent
    lateinit var playerFrameworkComponent: PlayerFrameworkComponent

    private lateinit var systemFrameworkComponent: SystemFrameworkComponent
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

        val downloadsFrameworkComponent = DaggerDownloadsFrameworkComponent.builder()
            .commonFrameworkComponent(commonFrameworkComponent)
            .build()

        val libraryFrameworkComponent = DaggerLibraryFrameworkComponent.builder()
            .commonFrameworkComponent(commonFrameworkComponent)
            .build()

        appComponent = DaggerAppComponent.builder()
            .commonFrameworkComponent(commonFrameworkComponent)
            .downloadsFrameworkComponent(downloadsFrameworkComponent)
            .libraryFrameworkComponent(libraryFrameworkComponent)
            .build()

        appComponent.thirdPartyIntegrator().initialize(this)

        playerFrameworkComponent = DaggerPlayerFrameworkComponent.builder()
            .commonFrameworkComponent(commonFrameworkComponent)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Update",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<UpdateAllShowsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RetryDownloads",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<RetryDownloadWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )
    }

    override fun commonFrameworkComponent(): CommonFrameworkComponent = commonFrameworkComponent
    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
    override fun authComponent(): AuthComponent = authComponent
    override fun playerFrameworkComponent(): PlayerFrameworkComponent = playerFrameworkComponent
}
