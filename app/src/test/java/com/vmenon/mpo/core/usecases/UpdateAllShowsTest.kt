package com.vmenon.mpo.core.usecases

import com.vmenon.mpo.downloads.domain.DownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.test.TestCoroutineRule
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class UpdateAllShowsTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val myLibraryService: MyLibraryService = mock()
    val downloadsService: DownloadsService = mock()
    val logger: Logger = mock()

    val updateAllShows = UpdateAllShows(myLibraryService, downloadsService, logger)

    @Test
    fun testShowsThatNeedUpdateNull() {
        testCoroutineRule.runBlockingTest {
            whenever(myLibraryService.getShowsSubscribedAndLastUpdatedBefore(any())).thenReturn(null)
            updateAllShows.invoke()
            verify(myLibraryService, times(0)).saveEpisode(any())
            verify(downloadsService, times(0)).queueDownload(any())
        }
    }

    @Test
    fun testShowsThatNeedUpdateEmpty() {
        testCoroutineRule.runBlockingTest {
            whenever(myLibraryService.getShowsSubscribedAndLastUpdatedBefore(any())).thenReturn(
                emptyList()
            )
            updateAllShows.invoke()
            verify(myLibraryService, times(0)).saveEpisode(any())
            verify(downloadsService, times(0)).queueDownload(any())
        }
    }

    @Test
    fun testShowsThatNeedUpdateHasUpdate() {
        testCoroutineRule.runBlockingTest {
            val show = TestData.show
            val showUpdate = TestData.showUpdate
            val savedEpisode = showUpdate.newEpisode.copy(id = 100L)

            whenever(myLibraryService.getShowsSubscribedAndLastUpdatedBefore(any())).thenReturn(
                listOf(show)
            )
            whenever(myLibraryService.getShowUpdate(show)).thenReturn(showUpdate)
            whenever(myLibraryService.saveEpisode(showUpdate.newEpisode)).thenReturn(savedEpisode)
            updateAllShows.invoke()
            verify(downloadsService).queueDownload(
                DownloadRequest(
                    downloadUrl = savedEpisode.downloadUrl,
                    downloadRequestType = DownloadRequestType.EPISODE,
                    name = savedEpisode.name,
                    imageUrl = savedEpisode.artworkUrl,
                    requesterId = savedEpisode.id
                )
            )
        }
    }

    @Test
    fun testShowsThatNeedUpdateNullUpdate() {
        testCoroutineRule.runBlockingTest {
            val show = TestData.show
            whenever(myLibraryService.getShowsSubscribedAndLastUpdatedBefore(any())).thenReturn(
                listOf(show)
            )
            whenever(myLibraryService.getShowUpdate(show)).thenReturn(null)
            updateAllShows.invoke()
            verify(myLibraryService, times(0)).saveEpisode(any())
            verify(downloadsService, times(0)).queueDownload(any())
        }
    }
}