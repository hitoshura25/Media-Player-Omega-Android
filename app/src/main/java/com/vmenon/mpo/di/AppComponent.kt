package com.vmenon.mpo.di

import com.vmenon.mpo.core.MPOMediaService
import com.vmenon.mpo.core.work.DownloadCompleteWorker
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.player.di.dagger.PlayerModule
import com.vmenon.mpo.repository.di.dagger.RepositoryModule

import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
        RepositoryModule::class,
        PlayerModule::class,
        ActivitySubcomponentsModule::class
    ]
)
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(service: MPOMediaService)
    fun inject(worker: UpdateAllShowsWorker)
    fun inject(worker: DownloadCompleteWorker)
    fun activityComponent(): ActivityComponent.Factory
}
