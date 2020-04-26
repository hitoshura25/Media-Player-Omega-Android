package com.vmenon.mpo.repository.di.dagger

import android.app.Application
import com.vmenon.mpo.repository.DownloadRepository
import com.vmenon.mpo.repository.EpisodeRepository
import com.vmenon.mpo.repository.ShowRepository
import com.vmenon.mpo.repository.ShowSearchRepository
import dagger.BindsInstance
import dagger.Component

@Component(modules = [RepositoryModule::class])
interface RepositoryComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun repositoryModule(module: RepositoryModule): Builder
        fun build(): RepositoryComponent
    }

    fun showSearchRepository(): ShowSearchRepository
    fun showRepository(): ShowRepository
    fun episodeRepository(): EpisodeRepository
    fun downloadRepository(): DownloadRepository
}