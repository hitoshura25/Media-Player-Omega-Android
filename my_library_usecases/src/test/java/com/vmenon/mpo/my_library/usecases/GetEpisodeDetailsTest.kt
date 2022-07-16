package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetEpisodeDetailsTest {
    @Test
    fun getEpisodeDetails() = runBlockingTest {
        val myLibraryService: MyLibraryService = mock()
        whenever(myLibraryService.getEpisode(1L)).thenReturn(TestData.episode)
        val usecase = GetEpisodeDetails(myLibraryService)

        assertEquals(
            listOf(LoadingState, SuccessState(TestData.episode)),
            usecase.invoke(1L).toList()
        )
    }
}