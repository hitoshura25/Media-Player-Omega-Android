package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.api.di.dagger.ApiModule
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.core.work.DownloadCompleteWorker
import com.vmenon.mpo.core.work.RetryDownloadWorker
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsModule
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryModule
import com.vmenon.mpo.login.di.LoginComponent
import com.vmenon.mpo.login.di.LoginModule
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponent
import com.vmenon.mpo.login.framework.di.LoginFrameworkModule
import com.vmenon.mpo.persistence.di.dagger.PersistenceModule
import com.vmenon.mpo.player.di.dagger.PlayerComponent
import com.vmenon.mpo.player.di.dagger.PlayerModule
import com.vmenon.mpo.player.framework.di.dagger.PlayerFrameworkComponent
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchModule

import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        ThirdPartyIntegratorModule::class,
        SubcomponentsModule::class,
        PersistenceModule::class,
        ApiModule::class,
        SearchModule::class,
        LibraryModule::class,
        DownloadsModule::class,
        PlayerModule::class,
        LoginModule::class,
        LoginFrameworkModule::class,
        NavigationModule::class
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

    fun inject(worker: UpdateAllShowsWorker)
    fun inject(worker: DownloadCompleteWorker)
    fun inject(worker: RetryDownloadWorker)
    fun thirdPartyIntegrator(): ThirdPartyIntegrator

    fun activityComponent(): ActivityComponent.Factory
    fun fragmentComponent(): FragmentComponent.Factory
    fun searchComponent(): SearchComponent.Factory
    fun downloadsComponent(): DownloadsComponent.Factory
    fun libraryComponent(): LibraryComponent.Factory
    fun playerComponent(): PlayerComponent.Factory
    fun loginComponent(): LoginComponent.Factory
    fun authComponent(): LoginFrameworkComponent.Factory
    fun playerFrameworkComponent(): PlayerFrameworkComponent.Factory
}
