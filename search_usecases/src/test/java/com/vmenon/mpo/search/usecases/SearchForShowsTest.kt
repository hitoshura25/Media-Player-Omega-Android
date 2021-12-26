package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.search.domain.ShowSearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SearchForShowsTest {
    private val showSearchService: ShowSearchService = mock()

    @Test
    fun searchForShows() = runBlockingTest {
        val usecase = SearchForShows(showSearchService)
        whenever(showSearchService.searchShows("keyword")).thenReturn(listOf(TestData.showSearchResultModel))
        assertEquals(listOf(TestData.showSearchResultModel), usecase.invoke("keyword"))
    }
}