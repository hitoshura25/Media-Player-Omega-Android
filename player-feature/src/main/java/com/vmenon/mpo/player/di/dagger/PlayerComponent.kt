package com.vmenon.mpo.player.di.dagger

import com.vmenon.mpo.player.framework.MPOMediaBrowserService
import com.vmenon.mpo.player.view.fragment.MediaPlayerFragment
import com.vmenon.mpo.player.viewmodel.MediaPlayerViewModel
import dagger.Subcomponent

@Subcomponent
@PlayerScope
interface PlayerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PlayerComponent
    }

    fun inject(fragment: MediaPlayerFragment)
    fun inject(viewModel: MediaPlayerViewModel)
    fun inject(service: MPOMediaBrowserService)
}