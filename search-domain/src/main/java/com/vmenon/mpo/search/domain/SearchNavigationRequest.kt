package com.vmenon.mpo.search.domain

import com.vmenon.mpo.navigation.domain.NavigationRequest

data class SearchNavigationRequest(
    override val destination: SearchNavigationDestination,
    override val params: SearchNavigationParams
) : NavigationRequest<SearchNavigationDestination, SearchNavigationParams>