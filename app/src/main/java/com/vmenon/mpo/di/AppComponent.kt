package com.vmenon.mpo.di

import com.vmenon.mpo.view.activity.DownloadsActivity
import com.vmenon.mpo.view.activity.EpisodeDetailsActivity
import com.vmenon.mpo.view.activity.LibraryActivity
import com.vmenon.mpo.view.activity.HomeActivity
import com.vmenon.mpo.view.activity.MediaPlayerActivity
import com.vmenon.mpo.view.activity.ShowDetailsActivity
import com.vmenon.mpo.view.activity.ShowSearchResultsActivity
import com.vmenon.mpo.core.MPOMediaService
import com.vmenon.mpo.core.work.DownloadCompleteWorker
import com.vmenon.mpo.core.work.UpdateAllShowsWorker
import com.vmenon.mpo.player.MPOPlayer
import com.vmenon.mpo.player.di.PlayerComponent
import com.vmenon.mpo.repository.di.RepositoryComponent

import dagger.Component

@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        RepositoryComponent::class,
        PlayerComponent::class
    ]
)
@AppScope
interface AppComponent {
    fun player(): MPOPlayer

    fun inject(service: MPOMediaService)

    fun inject(worker: UpdateAllShowsWorker)
    fun inject(worker: DownloadCompleteWorker)

    fun inject(activity: DownloadsActivity)
    fun inject(activity: EpisodeDetailsActivity)
    fun inject(activity: LibraryActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: MediaPlayerActivity)
    fun inject(activity: ShowDetailsActivity)
    fun inject(activity: ShowSearchResultsActivity)
}
