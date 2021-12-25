package com.vmenon.mpo.my_library.usecases

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class MyLibraryInteractorsTest {
    @Test
    fun interactors() {
        val getAllEpisodes: GetAllEpisodes = mock()
        val getEpisodeDetails: GetEpisodeDetails = mock()
        val getSubscribedShows: GetSubscribedShows = mock()
        val playEpisode: PlayEpisode = mock()
        val searchForShows: SearchForShows = mock()

        val interactors = MyLibraryInteractors(
            getAllEpisodes = getAllEpisodes,
            getEpisodeDetails = getEpisodeDetails,
            getSubscribedShows = getSubscribedShows,
            playEpisode = playEpisode,
            searchForShows = searchForShows
        )

        assertEquals(getAllEpisodes, interactors.getAllEpisodes)
        assertEquals(getEpisodeDetails, interactors.getEpisodeDetails)
        assertEquals(getSubscribedShows, interactors.getSubscribedShows)
        assertEquals(playEpisode, interactors.playEpisode)
        assertEquals(searchForShows, interactors.searchForShows)
    }
}