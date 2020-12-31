package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.domain.SearchNavigationLocation
import com.vmenon.mpo.search.domain.SearchNavigationParams

class SearchForShows(
    private val navigationController: NavigationController,
    private val searchDestination: NavigationDestination<SearchNavigationLocation>
) {
    suspend operator fun invoke(query: String, origin: NavigationOrigin<*>) {
        navigationController.navigate(
            origin,
            searchDestination,
            SearchNavigationParams(query)
        )
    }
}