package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowUpdateModel
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SubscribeToShowTest {
    private val myLibraryService: MyLibraryService = mock()
    private val downloadsService: DownloadsService = mock()

    @Test
    fun subscribeToShowHasShowUpdate() = runBlockingTest {
        val usecase = SubscribeToShow(myLibraryService, downloadsService)
        whenever(myLibraryService.saveShow(any())).thenReturn(TestData.show)
        whenever(myLibraryService.getShowUpdate(any())).thenReturn(ShowUpdateModel(TestData.episode))
        whenever(myLibraryService.saveEpisode(any())).thenReturn(TestData.episode)
        assertEquals(TestData.show, usecase.invoke(TestData.showSearchResultDetailsModel))
    }

    @Test
    fun subscribeToShowNoShowUpdate() = runBlockingTest {
        val usecase = SubscribeToShow(myLibraryService, downloadsService)
        whenever(myLibraryService.saveShow(any())).thenReturn(TestData.show)
        whenever(myLibraryService.getShowUpdate(any())).thenReturn(null)
        assertEquals(TestData.show, usecase.invoke(TestData.showSearchResultDetailsModel))
    }
}