package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.player.FileMediaSource
import com.vmenon.mpo.navigation.domain.player.Media
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationParams

class PlayEpisode(
    private val myLibraryService: MyLibraryService,
    private val navigationController: NavigationController,
    private val playerNavigationDestination: NavigationDestination<PlayerNavigationLocation>
) {
    suspend operator fun invoke(episodeId: Long, libraryView: NavigationOrigin<*>) {
        val episode = myLibraryService.getEpisode(episodeId)
        val filename = episode.filename ?: throw IllegalStateException("Filename cannot be null!")

        val params = PlayerNavigationParams(
            Media(
                mediaId = "episode:${episode.id}",
                mediaSource = FileMediaSource(filename),
                author = episode.show.author,
                album = episode.show.name,
                title = episode.name,
                artworkUrl = episode.artworkUrl ?: episode.show.artworkUrl,
                genres = episode.show.genres
            )
        )

        navigationController.navigate(
            libraryView,
            playerNavigationDestination,
            params
        )
    }
}