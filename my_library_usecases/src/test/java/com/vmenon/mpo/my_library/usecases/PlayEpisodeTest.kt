package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.player.FileMediaSource
import com.vmenon.mpo.navigation.domain.player.Media
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams
import com.vmenon.mpo.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class PlayEpisodeTest {
    private val myLibraryService: MyLibraryService = mock()
    private val navigationController: NavigationController = mock()
    private val playerNavigationDestination: NavigationDestination<PlayerNavigationLocation> =
        mock()

    private val usecase = PlayEpisode(
        myLibraryService,
        navigationController,
        playerNavigationDestination
    )

    @Test
    fun playEpisode() = runBlockingTest {
        val origin: NavigationOrigin<*> = mock()
        val paramsCaptor = argumentCaptor<PlayerNavigationParams>()
        whenever(myLibraryService.getEpisode(1L)).thenReturn(TestData.episode)
        usecase.invoke(1L, origin)
        verify(navigationController).navigate(
            eq(origin),
            eq(playerNavigationDestination),
            paramsCaptor.capture()
        )
        assertEquals(
            PlayerNavigationParams(
                Media(
                    mediaId = "episode:${TestData.episode.id}",
                    mediaSource = FileMediaSource(TestData.episode.filename!!),
                    author = TestData.episode.show.author,
                    album = TestData.episode.show.name,
                    title = TestData.episode.name,
                    artworkUrl = TestData.episode.artworkUrl,
                    genres = TestData.episode.show.genres
                )
            ),
            paramsCaptor.firstValue
        )
    }

    @Test
    fun playEpisodeUsesShowArtworkUrlIfEpisodeIsNull() = runBlockingTest {
        val origin: NavigationOrigin<*> = mock()
        val paramsCaptor = argumentCaptor<PlayerNavigationParams>()
        val episode = TestData.episode.copy(artworkUrl = null)
        whenever(myLibraryService.getEpisode(1L)).thenReturn(episode)
        usecase.invoke(1L, origin)
        verify(navigationController).navigate(
            eq(origin),
            eq(playerNavigationDestination),
            paramsCaptor.capture()
        )
        assertEquals(
            PlayerNavigationParams(
                Media(
                    mediaId = "episode:${episode.id}",
                    mediaSource = FileMediaSource(episode.filename!!),
                    author = episode.show.author,
                    album = episode.show.name,
                    title = episode.name,
                    artworkUrl = episode.show.artworkUrl,
                    genres = episode.show.genres
                )
            ),
            paramsCaptor.firstValue
        )
    }

    @Test(expected = IllegalStateException::class)
    fun playEpisodeThrowsExceptionIfFilenameNull() = runBlockingTest {
        val origin: NavigationOrigin<*> = mock()
        whenever(myLibraryService.getEpisode(1L)).thenReturn(TestData.episode.copy(filename = null))
        usecase.invoke(1L, origin)
    }
}