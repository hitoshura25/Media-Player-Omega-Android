package com.vmenon.mpo.downloads.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.downloads.domain.*
import com.vmenon.mpo.system.domain.ThreadUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetQueuedDownloadsUseCaseTest {
    private val threadUtil: ThreadUtil = mock()
    private val downloadsService: DownloadsService = mock()

    @Test
    fun flowEmitsLoadingAndSuccessStatePeriodically() = runBlockingTest {
        val firstDownloadsList = listOf(
            QueuedDownloadModel(
                download = DownloadModel(
                    name = "download",
                    downloadUrl = "www.download.com",
                    downloadQueueId = 1L,
                    downloadRequestType = DownloadRequestType.EPISODE,
                    requesterId = 1L,
                    downloadAttempt = 0,
                    imageUrl = null
                ),
                progress = 0,
                status = QueuedDownloadStatus.NOT_QUEUED,
                total = 0
            )
        )

        val secondDownloadsList = listOf(
            QueuedDownloadModel(
                download = DownloadModel(
                    name = "download",
                    downloadUrl = "www.download.com",
                    downloadQueueId = 1L,
                    downloadRequestType = DownloadRequestType.EPISODE,
                    requesterId = 1L,
                    downloadAttempt = 0,
                    imageUrl = null
                ),
                progress = 50,
                status = QueuedDownloadStatus.NOT_QUEUED,
                total = 0
            )
        )
        whenever(downloadsService.getAllQueued()).thenReturn(firstDownloadsList)
            .thenReturn(secondDownloadsList)

        val useCase = GetQueuedDownloads(downloadsService, threadUtil)
        val items = useCase.invoke().take(3).toList()
        assertEquals(LoadingState, items[0])
        assertEquals(SuccessState(firstDownloadsList), items[1])
        assertEquals(SuccessState(secondDownloadsList), items[2])
    }
}