package com.vmenon.mpo.di

import com.vmenon.mpo.view.activity.*
import com.vmenon.mpo.viewmodel.HomeViewModel
import com.vmenon.mpo.viewmodel.MediaPlayerViewModel
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivityComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(activity: HomeActivity)
    fun inject(activity: MediaPlayerActivity)

    fun inject(viewModel: HomeViewModel)
    fun inject(viewModel: MediaPlayerViewModel)
}