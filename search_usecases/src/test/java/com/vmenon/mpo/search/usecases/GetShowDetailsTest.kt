package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.search.domain.ShowSearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetShowDetailsTest {
    private val showSearchService: ShowSearchService = mock()

    @Test
    fun getShowDetails() = runBlockingTest {
        val usecase = GetShowDetails(showSearchService)
        whenever(showSearchService.getShowDetails(1L)).thenReturn(TestData.showSearchResultDetailsModel)
        assertEquals(TestData.showSearchResultDetailsModel, usecase.invoke(1L))
    }
}