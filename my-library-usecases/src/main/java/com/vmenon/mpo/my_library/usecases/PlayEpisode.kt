package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationController.Location.PLAYER
import com.vmenon.mpo.navigation.domain.NavigationParams.mediaIdParam
import com.vmenon.mpo.navigation.domain.NavigationView
import com.vmenon.mpo.player.domain.PlayerRequestMapper

class PlayEpisode(
    private val myLibraryService: MyLibraryService,
    private val requestMapper: PlayerRequestMapper<EpisodeModel>,
    private val navigationController: NavigationController
) {
    suspend operator fun invoke(episodeId: Long, libraryView: NavigationView) {
        val episodeModel = myLibraryService.getEpisode(episodeId)
        val mediaId = requestMapper.createMediaId(episodeModel)
        navigationController.onNavigationSelected(
            PLAYER,
            libraryView,
            mapOf(Pair(mediaIdParam, mediaId))
        )
    }
}