package com.vmenon.mpo.search.framework

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vmenon.mpo.api.model.Show
import com.vmenon.mpo.api.model.ShowDetails
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.test.TestCoroutineRule
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class MpoRetrofitApiSearchDataSourceTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val showDetails = ShowDetails(
        name = "show name",
        description = "show description",
        imageUrl = " show url",
        episodes = emptyList(),
    )

    private val retrofitApi: MediaPlayerOmegaRetrofitService = mock {
        on { searchPodcasts(any()) } doReturn Single.just(emptyList())
        on { getPodcastDetails(any(), any()) } doReturn Single.just(showDetails)
    }
    private val dataSource = MpoRetrofitApiSearchApiDataSource(retrofitApi)

    @Test
    fun whenSearchShowsCalledThenShouldCallService() = runTest {
        val result = dataSource.searchShows("search term")
        verify(retrofitApi).searchPodcasts("search term")
        assertEquals(emptyList<Show>(), result)
    }

    @Test
    fun whenGetShowDetailsCalledThenShouldCallService() = runTest {
        val details = dataSource.getShowDetails("feedUrl", 100)
        assertEquals(showDetails, details)
    }
}