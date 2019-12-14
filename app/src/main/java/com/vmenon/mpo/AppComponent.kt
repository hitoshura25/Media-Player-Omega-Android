package com.vmenon.mpo

import com.vmenon.mpo.activity.DownloadsActivity
import com.vmenon.mpo.activity.EpisodeDetailsActivity
import com.vmenon.mpo.activity.LibraryActivity
import com.vmenon.mpo.activity.MainActivity
import com.vmenon.mpo.activity.MediaPlayerActivity
import com.vmenon.mpo.activity.ShowDetailsActivity
import com.vmenon.mpo.activity.ShowSearchResultsActivity
import com.vmenon.mpo.core.BackgroundService
import com.vmenon.mpo.core.MPOMediaService

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(backgroundService: BackgroundService)
    fun inject(service: MPOMediaService)
    fun inject(activity: DownloadsActivity)
    fun inject(activity: EpisodeDetailsActivity)
    fun inject(activity: LibraryActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: MediaPlayerActivity)
    fun inject(activity: ShowDetailsActivity)
    fun inject(activity: ShowSearchResultsActivity)
}
