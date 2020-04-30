package com.vmenon.mpo.search.di.dagger

import com.vmenon.mpo.search.view.activity.ShowDetailsActivity
import com.vmenon.mpo.search.view.activity.ShowSearchResultsActivity
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
}