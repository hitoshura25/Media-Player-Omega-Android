package com.vmenon.mpo.search.domain

import com.vmenon.mpo.navigation.domain.NavigationParams
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavigationParams(val query: String) : NavigationParams