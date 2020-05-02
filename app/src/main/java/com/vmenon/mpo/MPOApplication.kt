package com.vmenon.mpo

import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.di.AppModule
import com.vmenon.mpo.di.DaggerAppComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import java.util.concurrent.TimeUnit

class MPOApplication : MultiDexApplication(),
    SearchComponentProvider, DownloadsComponentProvider, LibraryComponentProvider {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .thirdPartyIntegratorModule(ThirdPartyIntegratorModule())
            .build()

        appComponent.thirdPartyIntegrator().initialize(this)

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Update",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<UpdateAllShowsWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()
        )
    }

    override fun searchComponent(): SearchComponent = appComponent.searchComponent().create()
    override fun downloadsComponent(): DownloadsComponent =
        appComponent.downloadsComponent().create()

    override fun libraryComponent(): LibraryComponent = appComponent.libraryComponent().create()
}
