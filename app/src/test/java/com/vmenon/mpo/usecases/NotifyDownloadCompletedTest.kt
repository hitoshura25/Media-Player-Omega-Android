package com.vmenon.mpo.usecases

import com.vmenon.mpo.core.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.test.TestCoroutineRule
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class NotifyDownloadCompletedTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val downloadsService: DownloadsService = mock()
    val myLibraryService: MyLibraryService = mock()

    val notifyDownloadCompleted = NotifyDownloadCompleted(downloadsService, myLibraryService)

    @Test
    fun downloadIsNotEpisode() {
        testCoroutineRule.runBlockingTest {
            whenever(downloadsService.getCompletedDownloadByQueueId(1L)).thenReturn(
                TestData.completedDownload.copy(
                    download = TestData.completedDownload.download.copy(
                        downloadRequestType = DownloadRequestType.UNKNOWN,
                        id = 100L
                    )
                )
            )
            notifyDownloadCompleted.invoke(1L)
            verifyNoInteractions(myLibraryService)
            verify(downloadsService).delete(100L)
        }
    }

    @Test
    fun downloadIsEpisode() {
        testCoroutineRule.runBlockingTest {
            val completedDownload = TestData.completedDownload.copy(
                download = TestData.completedDownload.download.copy(
                    downloadRequestType = DownloadRequestType.EPISODE,
                    id = 100L,
                    requesterId = 34L
                )
            )
            whenever(downloadsService.getCompletedDownloadByQueueId(1L)).thenReturn(
                completedDownload
            )
            whenever(myLibraryService.getEpisode(34L)).thenReturn(TestData.episode)
            notifyDownloadCompleted.invoke(1L)
            verify(myLibraryService).saveEpisode(
                TestData.episode.copy(
                    filename = completedDownload.pathToFile
                )
            )
            verify(downloadsService).delete(100L)
        }
    }
}