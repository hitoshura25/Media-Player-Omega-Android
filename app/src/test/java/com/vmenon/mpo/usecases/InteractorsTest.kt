package com.vmenon.mpo.usecases

import com.vmenon.mpo.core.usecases.Interactors
import com.vmenon.mpo.core.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.core.usecases.RetryDownloads
import com.vmenon.mpo.core.usecases.UpdateAllShows
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class InteractorsTest {
    val updateAllShows: UpdateAllShows = mock()
    val retryDownloads: RetryDownloads = mock()
    val notifyDownloadCompleted: NotifyDownloadCompleted = mock()

    val interactors = Interactors(
        updateAllShows = updateAllShows,
        retryDownloads = retryDownloads,
        notifyDownloadCompleted = notifyDownloadCompleted
    )

    @Test
    fun interactors() {
        assertEquals(updateAllShows, interactors.updateAllShows)
        assertEquals(retryDownloads, interactors.retryDownloads)
        assertEquals(notifyDownloadCompleted, interactors.notifyDownloadCompleted)
    }
}