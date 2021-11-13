package com.vmenon.mpo.search.presentation.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.downloads.framework.di.dagger.DownloadsFrameworkComponent
import com.vmenon.mpo.my_library.framework.di.dagger.LibraryFrameworkComponent
import com.vmenon.mpo.search.framework.di.dagger.SearchFrameworkComponent
import com.vmenon.mpo.search.presentation.fragment.ShowDetailsFragment
import com.vmenon.mpo.search.presentation.fragment.ShowSearchResultsFragment
import com.vmenon.mpo.search.presentation.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.search.presentation.viewmodel.ShowSearchResultsViewModel
import dagger.Component

@Component(
    dependencies = [
        CommonFrameworkComponent::class,
        SearchFrameworkComponent::class,
        LibraryFrameworkComponent::class,
        DownloadsFrameworkComponent::class
    ],
    modules = [SearchModule::class]
)
@SearchScope
interface SearchComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun searchFrameworkComponent(component: SearchFrameworkComponent): Builder
        fun libraryFrameworkComponent(component: LibraryFrameworkComponent): Builder
        fun downloadsFrameworkComponent(component: DownloadsFrameworkComponent): Builder
        fun build(): SearchComponent
    }

    fun inject(fragment: ShowSearchResultsFragment)
    fun inject(fragment: ShowDetailsFragment)

    fun inject(viewModel: ShowSearchResultsViewModel)
    fun inject(viewModel: ShowDetailsViewModel)
}