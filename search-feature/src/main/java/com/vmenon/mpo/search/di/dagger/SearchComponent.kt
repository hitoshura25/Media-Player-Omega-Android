package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.search.view.activity.ShowDetailsActivity
import com.vmenon.mpo.search.view.activity.ShowSearchResultsActivity
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
    fun inject(activity: ShowSearchResultsActivity)

    fun inject(viewModel: ShowSearchResultsViewModel)
    fun inject(viewModel: ShowDetailsViewModel)
}