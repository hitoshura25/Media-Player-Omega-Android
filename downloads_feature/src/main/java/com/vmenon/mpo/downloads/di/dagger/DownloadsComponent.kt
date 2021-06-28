package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.downloads.framework.di.dagger.DownloadsFrameworkComponent
import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import com.vmenon.mpo.downloads.worker.DownloadCompleteWorker
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent
import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
        LibraryFrameworkComponent::class,
        DownloadsFrameworkComponent::class
    ],
    modules = [DownloadsModule::class]
)
@DownloadsScope
interface DownloadsComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun libraryFrameworkComponent(component: LibraryFrameworkComponent): Builder
        fun downloadsFrameworkComponent(component: DownloadsFrameworkComponent): Builder
        fun build(): DownloadsComponent
    }

    fun inject(fragment: DownloadsFragment)
    fun inject(viewModel: DownloadsViewModel)

    fun inject(worker: DownloadCompleteWorker)
}