package com.vmenon.mpo.downloads.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import com.vmenon.mpo.downloads.domain.QueuedDownloadStatus
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import com.vmenon.mpo.getOrAwaitValue
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DownloadsViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val queuedDownloads: GetQueuedDownloads = mock()
    lateinit var viewModel: DownloadsViewModel

    @Before
    fun setup() {
        viewModel = DownloadsViewModel()
        val interactors: DownloadsInteractors = mock()
        whenever(interactors.queuedDownloads).thenReturn(queuedDownloads)
        viewModel.downloadsInteractors = interactors
    }

    @Test
    fun queuedDownloads() {
        val queuedDownloadModel = QueuedDownloadModel(
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
        val result = SuccessState(listOf(queuedDownloadModel))
        val downloadsFlow = MutableStateFlow<ResultState<List<QueuedDownloadModel>>>(result)
        testCoroutineRule.runBlockingTest {
            whenever(queuedDownloads.invoke()).thenReturn(downloadsFlow)
            Assert.assertEquals(result, viewModel.downloads.getOrAwaitValue())
        }
    }
}