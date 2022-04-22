package com.vmenon.mpo.usecases

import com.vmenon.mpo.core.usecases.RetryDownloads
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.test.TestCoroutineRule
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class RetryDownloadsTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val downloadsService: DownloadsService = mock()
    val logger: Logger = mock()

    val retryDownloads = RetryDownloads(downloadsService, 3, logger)

    @Test
    fun noQueuedDownloads() {
        testCoroutineRule.runBlockingTest {
            whenever(downloadsService.getAllQueued()).thenReturn(emptyList())
            retryDownloads.invoke()
            verify(downloadsService, times(0)).retryDownload(any())
        }
    }

    @Test
    fun noFailedOrNotQueuedDownloads() {
        testCoroutineRule.runBlockingTest {
            val queuedDownload = TestData.queuedDownload.copy(status = QueuedDownloadStatus.RUNNING)
            whenever(downloadsService.getAllQueued()).thenReturn(listOf(queuedDownload))
            retryDownloads.invoke()
            verify(downloadsService, times(0)).retryDownload(any())
        }
    }

    @Test
    fun hasNotQueuedDownloadButAtRetryLimit() {
        testCoroutineRule.runBlockingTest {
            val queuedDownload = TestData.queuedDownload.copy(
                status = QueuedDownloadStatus.NOT_QUEUED,
                download = TestData.download.copy(downloadAttempt = 3)
            )
            whenever(downloadsService.getAllQueued()).thenReturn(listOf(queuedDownload))
            retryDownloads.invoke()
            verify(downloadsService, times(0)).retryDownload(any())
        }
    }

    @Test
    fun hasNotQueuedDownloadAndBelowRetryLimit() {
        testCoroutineRule.runBlockingTest {
            val queuedDownload = TestData.queuedDownload.copy(
                status = QueuedDownloadStatus.NOT_QUEUED,
                download = TestData.download.copy(downloadAttempt = 0)
            )
            whenever(downloadsService.getAllQueued()).thenReturn(listOf(queuedDownload))
            retryDownloads.invoke()
            verify(downloadsService).retryDownload(queuedDownload.download)
        }
    }
    @Test
    fun hasNotFailedDownloadButAtRetryLimit() {
        testCoroutineRule.runBlockingTest {
            val queuedDownload = TestData.queuedDownload.copy(
                status = QueuedDownloadStatus.FAILED,
                download = TestData.download.copy(downloadAttempt = 3)
            )
            whenever(downloadsService.getAllQueued()).thenReturn(listOf(queuedDownload))
            retryDownloads.invoke()
            verify(downloadsService, times(0)).retryDownload(any())
        }
    }

    @Test
    fun hasFailedDownloadAndBelowRetryLimit() {
        testCoroutineRule.runBlockingTest {
            val queuedDownload = TestData.queuedDownload.copy(
                status = QueuedDownloadStatus.FAILED,
                download = TestData.download.copy(downloadAttempt = 0)
            )
            whenever(downloadsService.getAllQueued()).thenReturn(listOf(queuedDownload))
            retryDownloads.invoke()
            verify(downloadsService).retryDownload(queuedDownload.download)
        }
    }
}