package com.vmenon.mpo.player.di.dagger

import com.vmenon.mpo.player.framework.MPOMediaService
import com.vmenon.mpo.player.view.activity.MediaPlayerActivity
import com.vmenon.mpo.player.viewmodel.MediaPlayerViewModel
import dagger.Subcomponent

@Subcomponent
@PlayerScope
interface PlayerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PlayerComponent
    }

    fun inject(activity: MediaPlayerActivity)
    fun inject(viewModel: MediaPlayerViewModel)
    fun inject(service: MPOMediaService)
}