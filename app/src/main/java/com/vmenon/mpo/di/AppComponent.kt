package com.vmenon.mpo.di

import com.vmenon.mpo.view.activity.DownloadsActivity
import com.vmenon.mpo.view.activity.EpisodeDetailsActivity
import com.vmenon.mpo.view.activity.LibraryActivity
import com.vmenon.mpo.view.activity.HomeActivity
import com.vmenon.mpo.view.activity.MediaPlayerActivity
import com.vmenon.mpo.view.activity.ShowDetailsActivity
import com.vmenon.mpo.view.activity.ShowSearchResultsActivity
import com.vmenon.mpo.core.MPOMediaService
import com.vmenon.mpo.core.work.UpdateAllShowsWorker

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        RoomModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun inject(service: MPOMediaService)

    fun inject(worker: UpdateAllShowsWorker)

    fun inject(activity: DownloadsActivity)
    fun inject(activity: EpisodeDetailsActivity)
    fun inject(activity: LibraryActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: MediaPlayerActivity)
    fun inject(activity: ShowDetailsActivity)
    fun inject(activity: ShowSearchResultsActivity)
}
