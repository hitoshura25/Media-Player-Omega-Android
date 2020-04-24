package com.vmenon.mpo.repository.di

import android.app.Application
import com.vmenon.mpo.api.di.ApiComponent
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import com.vmenon.mpo.repository.ShowSearchRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        RepositoryModule::class
    ],
    dependencies = [
        ApiComponent::class
    ]
)
@Singleton
interface RepositoryComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun apiComponent(component: ApiComponent): Builder
        fun build(): RepositoryComponent
    }

    fun showSearchRepository(): ShowSearchRepository
    fun showRepository(): ShowRepository
    fun episodeRepository(): EpisodeRepository
    fun downloadRepository(): DownloadRepository
}