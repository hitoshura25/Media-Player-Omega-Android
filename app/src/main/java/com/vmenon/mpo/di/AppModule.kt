package com.vmenon.mpo.di

import android.app.Application
import androidx.lifecycle.ViewModel

import com.vmenon.mpo.core.*
import com.vmenon.mpo.core.player.MPOExoPlayer
import com.vmenon.mpo.core.player.MPOPlayer
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.core.repository.MPORepository
import com.vmenon.mpo.viewmodel.ViewModelFactory

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun providesApplication(): Application = application

    @Provides
    @Singleton
    fun provideDownloadManager(
        mpoRepository: MPORepository,
        downloadRepository: DownloadRepository,
        episodeRepository: EpisodeRepository
    ): DownloadManager {
        return DownloadManager(
            application.applicationContext,
            mpoRepository,
            downloadRepository,
            episodeRepository
        )
    }

    @Provides
    @Singleton
    fun providePlayer(): MPOPlayer =
        MPOExoPlayer(application)

    @Singleton
    @Provides
    fun provideViewModelFactory(
        creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelFactory = ViewModelFactory(creators)

    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = DefaultSchedulerProvider()
}
