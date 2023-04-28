package com.vmenon.mpo.search.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.getOrAwaitValue
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEffect
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEffect.DownloadQueuedViewEffect
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEffect.ShowSubscribedViewEffect
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEvent
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEvent.LoadShowDetailsEvent
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEvent.QueueDownloadEvent
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewEvent.SubscribeToShowEvent
import com.vmenon.mpo.search.presentation.mvi.ShowDetailsViewState
import com.vmenon.mpo.search.usecases.GetShowDetails
import com.vmenon.mpo.search.usecases.QueueDownloadForShow
import com.vmenon.mpo.search.usecases.SearchForShows
import com.vmenon.mpo.search.usecases.SubscribeToShow
import com.vmenon.mpo.test.TestCoroutineRule
import com.vmenon.mpo.test.TestData
import com.vmenon.mpo.test.TestData.SHOW_RESULT_ID
import com.vmenon.mpo.test.TestData.download
import com.vmenon.mpo.test.TestData.show
import com.vmenon.mpo.test.TestData.showSearchResultDetailsModel
import com.vmenon.mpo.test.TestData.showSearchResultEpisodeModel
import com.vmenon.mpo.test.TestData.showSearchResultModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ShowDetailsViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val searchForShows: SearchForShows = mock {

    }
    private val getShowDetails: GetShowDetails = mock {
        onBlocking { invoke(SHOW_RESULT_ID) } doReturn showSearchResultDetailsModel
    }
    private val subscribeToShow: SubscribeToShow = mock {
        onBlocking { invoke(showSearchResultDetailsModel) } doReturn show
    }
    private val queueDownloadForShow: QueueDownloadForShow = mock {
        onBlocking { invoke(showSearchResultModel, showSearchResultEpisodeModel) } doReturn download
    }

    private val viewmodel = ShowDetailsViewModel().apply {
        this.searchInteractors = mock {
            on { searchForShows } doReturn searchForShows
            on { getShowDetails } doReturn getShowDetails
            on { subscribeToShow } doReturn subscribeToShow
            on { queueDownloadForShow } doReturn queueDownloadForShow
        }
    }

    @Test
    fun givenShowDetailsReturnedWhenLoadShowDetailsEventSentThenStateHasDetails() {
        viewmodel.send(LoadShowDetailsEvent(SHOW_RESULT_ID))
        assertEquals(
            ShowDetailsViewState(
                showDetails = showSearchResultDetailsModel,
                loading = false,
                error = false,
            ),
            viewmodel.states().getOrAwaitValue().anyContent()
        )
    }

    @Test
    fun givenShowDetailsErroredWhenLoadShowDetailsEventSentThenStateHasError() = runTest {
        whenever(getShowDetails.invoke(SHOW_RESULT_ID)).thenThrow(IllegalArgumentException())
        viewmodel.send(LoadShowDetailsEvent(SHOW_RESULT_ID))
        assertEquals(
            ShowDetailsViewState(
                showDetails = null,
                loading = false,
                error = true,
            ),
            viewmodel.states().getOrAwaitValue().anyContent()
        )
    }

    @Test
    fun whenSubscribeToShowEventSentThenStateAndEffectsEmitsCorrectValues() = runTest {
        viewmodel.send(SubscribeToShowEvent(showSearchResultDetailsModel))
        assertEquals(
            ShowSubscribedViewEffect(
                subscribedShow = show,
            ),
            viewmodel.effects().getOrAwaitValue().anyContent()
        )
        assertEquals(
            ShowDetailsViewState(
                showDetails = showSearchResultDetailsModel.copy(subscribed = true),
                loading = false,
                error = false,
            ),
            viewmodel.states().getOrAwaitValue().anyContent()
        )
    }

    @Test
    fun whenQueueForDownloadEventSentThenEffectsEmitsCorrectValues() = runTest {
        viewmodel.send(
            QueueDownloadEvent(
                show = showSearchResultModel,
                episode = showSearchResultEpisodeModel
            )
        )
        assertEquals(
            DownloadQueuedViewEffect(
                queuedDownload = download
            ),
            viewmodel.effects().getOrAwaitValue().anyContent()
        )
    }
}