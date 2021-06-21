package com.vmenon.mpo.downloads.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.downloads.domain.DownloadsService
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [DownloadsFrameworkModule::class]
)
@DownloadsFrameworkScope
interface DownloadsFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun build(): DownloadsFrameworkComponent
    }
    fun downloadsService(): DownloadsService
}