package com.vmenon.mpo.di

import com.mpo.core.di.ThirdPartyIntegratorModule
import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.core.ThirdPartyIntegrator
import com.vmenon.mpo.core.work.DownloadCompleteWorker
import com.vmenon.mpo.core.work.RetryDownloadWorker
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.downloads.framework.di.dagger.DownloadsFrameworkComponent
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent

import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
        LibraryFrameworkComponent::class,
        DownloadsFrameworkComponent::class
    ],
    modules = [
        AppModule::class,
        ThirdPartyIntegratorModule::class
    ]
)
@AppScope
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun libraryFrameworkComponent(component: LibraryFrameworkComponent): Builder
        fun downloadsFrameworkComponent(component: DownloadsFrameworkComponent): Builder
        fun build(): AppComponent
    }

    fun thirdPartyIntegrator(): ThirdPartyIntegrator
    fun activityComponent(): ActivityComponent.Factory

    fun inject(worker: UpdateAllShowsWorker)
    fun inject(worker: RetryDownloadWorker)
    fun inject(worker: DownloadCompleteWorker)
}
