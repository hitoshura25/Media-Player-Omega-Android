package com.vmenon.mpo.search.usecases

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class SearchInteractorsTest {
    @Test
    fun interactors() {
        val searchForShows: SearchForShows = mock()
        val getShowDetails: GetShowDetails = mock()
        val queueDownloadForShow: QueueDownloadForShow = mock()
        val subscribeToShow: SubscribeToShow = mock()
        val interactors = SearchInteractors(
            searchForShows = searchForShows,
            getShowDetails = getShowDetails,
            queueDownloadForShow = queueDownloadForShow,
            subscribeToShow = subscribeToShow,
        )
        assertEquals(searchForShows, interactors.searchForShows)
        assertEquals(getShowDetails, interactors.getShowDetails)
        assertEquals(queueDownloadForShow, interactors.queueDownloadForShow)
        assertEquals(subscribeToShow, interactors.subscribeToShow)
    }
}