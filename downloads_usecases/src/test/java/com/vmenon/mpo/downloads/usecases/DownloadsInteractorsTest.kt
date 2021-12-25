package com.vmenon.mpo.downloads.usecases

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class DownloadsInteractorsTest {
    @Test
    fun interactors() {
        val getQueuedDownloads: GetQueuedDownloads = mock()
        val interactors = DownloadsInteractors(getQueuedDownloads)
        assertEquals(getQueuedDownloads, interactors.queuedDownloads)
    }
}