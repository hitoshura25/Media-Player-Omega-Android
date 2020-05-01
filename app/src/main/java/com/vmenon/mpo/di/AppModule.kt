package com.vmenon.mpo.di

import android.app.Application
import androidx.lifecycle.ViewModel
import com.vmenon.mpo.rx.scheduler.DefaultSchedulerProvider
import com.vmenon.mpo.rx.scheduler.SchedulerProvider

import com.vmenon.mpo.core.*
import com.vmenon.mpo.core.navigation.DefaultNavigationController
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.navigation.NavigationController
import com.vmenon.mpo.shows.ShowUpdateManager
import com.vmenon.mpo.shows.repository.EpisodeRepository
import com.vmenon.mpo.shows.repository.ShowRepository
import com.vmenon.mpo.viewmodel.ViewModelFactory

import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
class AppModule(private val application: Application) {
    @Provides
    fun providesApplication(): Application = application

    @Provides
    fun provideViewModelFactory(
        creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelFactory =
        ViewModelFactory(creators)

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider =
        DefaultSchedulerProvider()

    @Provides
    fun provideShowUpdateManager(
        episodeRepository: EpisodeRepository,
        showRepository: ShowRepository,
        downloadRepository: DownloadRepository
    ): ShowUpdateManager = DefaultShowUpdateManager(
        showRepository,
        episodeRepository,
        downloadRepository
    )

    @Provides
    fun providesNavigationController(): NavigationController = DefaultNavigationController()
}
