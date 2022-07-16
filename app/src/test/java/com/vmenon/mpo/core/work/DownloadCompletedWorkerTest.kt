package com.vmenon.mpo.core.work

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import com.vmenon.mpo.core.usecases.Interactors
import com.vmenon.mpo.di.AppComponentProvider
import com.vmenon.mpo.test.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DownloadCompletedWorkerTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val workerParams: WorkerParameters = mock()

    val interactors = Interactors(
        updateAllShows = mock(),
        retryDownloads = mock(),
        notifyDownloadCompleted = mock()
    )

    val app: Context = mock(extraInterfaces = arrayOf(AppComponentProvider::class))
    val data: Data = mock()

    lateinit var downloadCompletedWorker: DownloadCompleteWorker

    @Before
    fun setup() {
        val taskExecutor: TaskExecutor = mock()
        whenever(taskExecutor.backgroundExecutor).thenReturn(mock())
        whenever(workerParams.taskExecutor).thenReturn(taskExecutor)
        whenever(workerParams.backgroundExecutor).thenReturn(mock())
        whenever(workerParams.inputData).thenReturn(data)
        whenever((app as AppComponentProvider).appComponent()).thenReturn(mock())

        downloadCompletedWorker = DownloadCompleteWorker(app, workerParams)
        downloadCompletedWorker.logger = mock()
        downloadCompletedWorker.interactors = interactors
        assertEquals(interactors, downloadCompletedWorker.interactors)
    }

    @Test
    fun invokeUseCaseIfDownloadIdValid() {
        testCoroutineRule.runBlockingTest {
            whenever(data.getLong(DownloadCompleteWorker.INPUT_DOWNLOAD_ID, -1L)).thenReturn(100L)
            assertEquals(ListenableWorker.Result.success(), downloadCompletedWorker.doWork())
            verify(interactors.notifyDownloadCompleted).invoke(100L)
        }
    }

    @Test
    fun doNotInvokeUseCaseIfDownloadIdInvalid() {
        testCoroutineRule.runBlockingTest {
            whenever(data.getLong(DownloadCompleteWorker.INPUT_DOWNLOAD_ID, -1L)).thenReturn(-1L)
            assertEquals(ListenableWorker.Result.success(), downloadCompletedWorker.doWork())
            verifyNoInteractions(interactors.notifyDownloadCompleted)
        }
    }
}