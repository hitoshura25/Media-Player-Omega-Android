package com.vmenon.mpo.di

import com.vmenon.mpo.view.activity.*
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(activity: DownloadsActivity)
    fun inject(activity: EpisodeDetailsActivity)
    fun inject(activity: LibraryActivity)
    fun inject(activity: HomeActivity)
    fun inject(activity: MediaPlayerActivity)

}