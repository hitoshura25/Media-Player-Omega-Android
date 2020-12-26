package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.domain.SearchNavigationDestination
import com.vmenon.mpo.search.domain.SearchNavigationParams
import com.vmenon.mpo.search.domain.SearchNavigationRequest

class SearchForShows(
    private val navigationController: NavigationController,
    private val searchDestination: SearchNavigationDestination
) {
    suspend operator fun invoke(query: String, origin: NavigationOrigin<*>) {
        navigationController.onNavigationSelected(
            SearchNavigationRequest(searchDestination, SearchNavigationParams(query)),
            origin
        )
    }
}