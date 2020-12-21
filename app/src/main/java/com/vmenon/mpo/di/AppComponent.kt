package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.api.di.dagger.ApiModule
import com.vmenon.mpo.core.MPOMediaService
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.core.work.DownloadCompleteWorker
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsModule
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryModule
import com.vmenon.mpo.persistence.di.dagger.PersistenceModule
import com.vmenon.mpo.player.di.dagger.PlayerModule
import com.vmenon.mpo.repository.di.dagger.RepositoryModule
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchModule

import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        PlayerModule::class,
        ThirdPartyIntegratorModule::class,
        SubcomponentsModule::class,
        RepositoryModule::class,
        PersistenceModule::class,
        ApiModule::class,
        SearchModule::class,
        LibraryModule::class,
        DownloadsModule::class
    ]
)
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun thirdPartyIntegratorModule(module: ThirdPartyIntegratorModule): Builder
        fun build(): AppComponent
    }

    fun inject(service: MPOMediaService)
    fun inject(worker: UpdateAllShowsWorker)
    fun inject(worker: DownloadCompleteWorker)
    fun thirdPartyIntegrator(): ThirdPartyIntegrator

    fun activityComponent(): ActivityComponent.Factory
    fun searchComponent(): SearchComponent.Factory
    fun downloadsComponent(): DownloadsComponent.Factory
    fun libraryComponent(): LibraryComponent.Factory
}
