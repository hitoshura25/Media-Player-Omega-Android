package com.vmenon.mpo.search.presentation.adapter.diff

import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.test.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShowSearchResultsDiffTest {
    private val oldSearchResults = ArrayList<ShowSearchResultModel>()
    private val newSearchResults = ArrayList<ShowSearchResultModel>()

    private val showSearchResultsDiff = ShowSearchResultsDiff(oldSearchResults, newSearchResults)

    @Test
    fun givenNameIsSameThenItemsSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel)
        assertTrue(showSearchResultsDiff.areItemsTheSame(0, 0))
    }

    @Test
    fun givenNameIsDifferentThenItemsDifferent() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(name = "DifferentName"))
        assertFalse(showSearchResultsDiff.areItemsTheSame(0, 0))
    }

    @Test
    fun oldItemsLength() {
        assertEquals(0, showSearchResultsDiff.oldListSize)
        oldSearchResults.add(TestData.showSearchResultModel)
        assertEquals(1, showSearchResultsDiff.oldListSize)
    }

    @Test
    fun newItemsLength() {
        assertEquals(0, showSearchResultsDiff.newListSize)
        newSearchResults.add(TestData.showSearchResultModel)
        assertEquals(1, showSearchResultsDiff.newListSize)
    }

    @Test
    fun givenFieldsMatchThenContentsSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel)
        assertTrue(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenNameDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(name = "Different Name"))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenArtworkUrlDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(artworkUrl = "differentArtworkurl"))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenAuthorDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(author = "Different Author"))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenDescriptionDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(description = "Different Description"))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenFeedUrlDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(feedUrl = "differentFeedUrl"))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }

    @Test
    fun givenGenresDoesNotMatchThenContentsNotSame() {
        oldSearchResults.add(TestData.showSearchResultModel)
        newSearchResults.add(TestData.showSearchResultModel.copy(genres = listOf("Different Genre")))
        assertFalse(showSearchResultsDiff.areContentsTheSame(0, 0))
    }
}