package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.search.view.activity.ShowDetailsActivity
import com.vmenon.mpo.search.view.fragment.ShowDetailsFragment
import com.vmenon.mpo.search.view.fragment.ShowSearchResultsFragment
import com.vmenon.mpo.search.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.search.viewmodel.ShowSearchResultsViewModel
import dagger.Subcomponent

@Subcomponent
@SearchScope
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(activity: ShowDetailsActivity)
    fun inject(fragment: ShowSearchResultsFragment)
    fun inject(fragment: ShowDetailsFragment)

    fun inject(viewModel: ShowSearchResultsViewModel)
    fun inject(viewModel: ShowDetailsViewModel)
}