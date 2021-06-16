package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import dagger.Subcomponent

@Subcomponent
interface DownloadsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DownloadsComponent
    }

    fun inject(fragment: DownloadsFragment)
    fun inject(viewModel: DownloadsViewModel)
}