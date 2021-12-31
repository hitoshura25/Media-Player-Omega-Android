package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.MyLibraryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetSubscribedShowsTest {
    private val myLibraryService: MyLibraryService = mock()

    @Test
    fun getAllSubscribedShowsFlow() = runBlockingTest {
        val usecase = GetSubscribedShows(myLibraryService)
        whenever(myLibraryService.getAllSubscribedShows()).thenReturn(listOf(TestData.show))
        assertEquals(
            listOf(LoadingState, SuccessState(listOf(TestData.show))),
            usecase.invoke().toList()
        )
    }
}