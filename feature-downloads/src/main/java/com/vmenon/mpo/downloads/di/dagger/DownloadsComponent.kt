package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import dagger.Subcomponent

@Subcomponent
interface DownloadsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DownloadsComponent
    }

    fun inject(activity: DownloadsActivity)
    fun inject(viewModel: DownloadsViewModel)
}