package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetAllEpisodesTest {

    private val myLibraryService: MyLibraryService = mock()

    @Test
    fun getAllEpisodesFlow() = runBlockingTest {
        val usecase = GetAllEpisodes(myLibraryService)
        val episodes = listOf(TestData.episode)
        whenever(myLibraryService.getAllEpisodes()).thenReturn(episodes)
        assertEquals(
            listOf(LoadingState, SuccessState(episodes)),
            usecase.invoke().toList()
        )
    }
}