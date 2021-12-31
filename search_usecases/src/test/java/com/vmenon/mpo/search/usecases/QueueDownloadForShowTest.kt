package com.vmenon.mpo.search.usecases

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class QueueDownloadForShowTest {
    private val myLibraryService: MyLibraryService = mock()
    private val downloadsService: DownloadsService = mock()

    @Test
    fun queueDownloadForShowSavesShowAndEpisodeIfNotExists() = runBlockingTest {
        val usecase = QueueDownloadForShow(myLibraryService, downloadsService)
        whenever(myLibraryService.getShowByName(TestData.showSearchResultModel.name)).thenReturn(
            null
        )
        whenever(myLibraryService.saveShow(any())).thenReturn(TestData.show)
        whenever(
            myLibraryService.getEpisodeByName(
                TestData.showSearchResultEpisodeModel.name
            )
        ).thenReturn(null)
        whenever(myLibraryService.saveEpisode(any())).thenReturn(TestData.episode)
        whenever(downloadsService.queueDownload(any())).thenReturn(TestData.download)
        assertEquals(
            TestData.download,
            usecase.invoke(TestData.showSearchResultModel, TestData.showSearchResultEpisodeModel)
        )
    }

    @Test
    fun queueDownloadForShowSavesShowIfNotExistsButUsesExistingEpisode() = runBlockingTest {
        val usecase = QueueDownloadForShow(myLibraryService, downloadsService)
        whenever(myLibraryService.getShowByName(TestData.showSearchResultModel.name)).thenReturn(
            null
        )
        whenever(myLibraryService.saveShow(any())).thenReturn(TestData.show)
        whenever(
            myLibraryService.getEpisodeByName(
                TestData.showSearchResultEpisodeModel.name
            )
        ).thenReturn(TestData.episode)
        whenever(downloadsService.queueDownload(any())).thenReturn(TestData.download)
        assertEquals(
            TestData.download,
            usecase.invoke(TestData.showSearchResultModel, TestData.showSearchResultEpisodeModel)
        )
    }

    @Test
    fun queueDownloadUsesExistingShowSavesEpisodeIfNotExists() = runBlockingTest {
        val usecase = QueueDownloadForShow(myLibraryService, downloadsService)
        whenever(myLibraryService.getShowByName(TestData.showSearchResultModel.name)).thenReturn(
            TestData.show
        )
        whenever(
            myLibraryService.getEpisodeByName(
                TestData.showSearchResultEpisodeModel.name
            )
        ).thenReturn(null)
        whenever(myLibraryService.saveEpisode(any())).thenReturn(TestData.episode)
        whenever(downloadsService.queueDownload(any())).thenReturn(TestData.download)
        assertEquals(
            TestData.download,
            usecase.invoke(TestData.showSearchResultModel, TestData.showSearchResultEpisodeModel)
        )
    }
}