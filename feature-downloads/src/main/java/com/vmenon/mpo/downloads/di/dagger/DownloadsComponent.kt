package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.downloads.view.activity.DownloadsActivity
import com.vmenon.mpo.viewmodel.ViewModelFactory
import dagger.Subcomponent

@Subcomponent(modules = [ViewModelModule::class])
interface DownloadsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DownloadsComponent
    }

    fun inject(activity: DownloadsActivity)
    fun viewModelFactory(): ViewModelFactory
}