package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import dagger.Component

@DownloadsScope
@Component(
    dependencies = [AppComponent::class, LibraryComponent::class],
    modules = [DownloadsModule::class]
)
interface DownloadsComponent {
    fun inject(fragment: DownloadsFragment)
    fun inject(viewModel: DownloadsViewModel)
}