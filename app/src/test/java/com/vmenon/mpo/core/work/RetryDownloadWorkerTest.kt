package com.vmenon.mpo.core.work

import android.content.Context
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
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RetryDownloadWorkerTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    val workerParams: WorkerParameters = mock()

    val interactors = Interactors(
        updateAllShows = mock(),
        retryDownloads = mock(),
        notifyDownloadCompleted = mock()
    )

    val app: Context = mock(extraInterfaces = arrayOf(AppComponentProvider::class))

    lateinit var retryDownloadWorker: RetryDownloadWorker

    @Before
    fun setup() {
        val taskExecutor: TaskExecutor = mock()
        whenever(taskExecutor.backgroundExecutor).thenReturn(mock())
        whenever(workerParams.taskExecutor).thenReturn(taskExecutor)
        whenever(workerParams.backgroundExecutor).thenReturn(mock())
        whenever((app as AppComponentProvider).appComponent()).thenReturn(mock())
        retryDownloadWorker = RetryDownloadWorker(app, workerParams)
        retryDownloadWorker.interactors = interactors
        assertEquals(interactors, retryDownloadWorker.interactors)
    }

    @Test
    fun invokeUseCase() {
        testCoroutineRule.runBlockingTest {
            assertEquals(ListenableWorker.Result.success(), retryDownloadWorker.doWork())
            verify(interactors.retryDownloads).invoke()
        }
    }
}