package com.vmenon.mpo.search.repository.impl

import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.api.model.ShowDetails
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.persistence.ShowSearchPersistence
import com.vmenon.mpo.shows.persistence.ShowPersistence
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ShowSearchRepositoryImplTest {
    @Mock
    lateinit var api: MediaPlayerOmegaApi

    @Mock
    lateinit var showPersistence: ShowPersistence

    @Mock
    lateinit var showSearchPersistence: ShowSearchPersistence

    lateinit var repository: ShowSearchRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = ShowSearchRepositoryImpl(api, showSearchPersistence, showPersistence)
    }

    @Test
    fun `when getting search results for term should return flowable from showSearchPersistence`() {
        val peristenceFlowable = Flowable.just(
            listOf(
                ShowSearchResultModel(
                    name = "Show Name",
                    description = "Show Description",
                    artworkUrl = "",
                    feedUrl = "http://feed.com/rss",
                    author = "",
                    genres = emptyList()
                )
            )
        )
        `when`(
            showSearchPersistence.getBySearchTermOrderedByName("test")
        ).thenReturn(peristenceFlowable)

        val returnedFlowable = repository.getShowSearchResultsForTermOrderedByName("test")
        assertEquals(returnedFlowable, peristenceFlowable)
    }

    @Test
    fun `when getting show details should fetch from showSearchPersistence and from api and check for subscribed in showPersistence`() {
        val searchResultId = 101L
        val showSearchResult = ShowSearchResultModel(
            name = "Show Name",
            description = "Show Description",
            artworkUrl = "",
            feedUrl = "http://feed.com/rss",
            author = "",
            genres = emptyList()
        )
        `when`(showSearchPersistence.getSearchResultById(searchResultId)).thenReturn(
            Flowable.just(showSearchResult)
        )
        `when`(api.getPodcastDetails(showSearchResult.feedUrl, 10)).thenReturn(
            Single.just(
                ShowDetails(
                    name = "Show Name",
                    description = "description",
                    episodes = emptyList(),
                    imageUrl = ""
                )
            )
        )
        `when`(showPersistence.getByName(showSearchResult.name)).thenReturn(
            Maybe.just(
                ShowModel(
                    id = 201L,
                    isSubscribed = true,
                    description = "",
                    name = "Show Name",
                    genres = emptyList(),
                    author = "",
                    feedUrl = "",
                    artworkUrl = "",
                    lastEpisodePublished = 0L,
                    lastUpdate = 0L
                )
            )
        )
        val showDetails = repository.getShowDetails(searchResultId).test().values().first()
        assertEquals(showSearchResult, showDetails.show)
        assertTrue(showDetails.episodes.isEmpty())
        assertTrue(showDetails.subscribed)
    }
}