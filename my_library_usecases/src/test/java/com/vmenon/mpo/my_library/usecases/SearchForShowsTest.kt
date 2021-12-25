package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationParams
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class SearchForShowsTest {
    private val navigationController: NavigationController = mock()
    private val searchDestination: NavigationDestination<SearchNavigationLocation> = mock()

    @Test
    fun searchForShows() = runBlockingTest {
        val usecase = SearchForShows(navigationController, searchDestination)
        val origin: NavigationOrigin<*> = mock()
        usecase.invoke("keyword", origin)
        verify(navigationController).navigate(
            origin,
            searchDestination,
            SearchNavigationParams("keyword")
        )
    }
}