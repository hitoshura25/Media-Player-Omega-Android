package com.vmenon.mpo.search.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.search.domain.ShowSearchService
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [SearchFrameworkModule::class]
)
@SearchFrameworkScope
interface SearchFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun build(): SearchFrameworkComponent
    }

    fun showSearchService(): ShowSearchService
}