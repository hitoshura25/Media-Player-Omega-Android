package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerNavigationParams
import com.vmenon.mpo.player.domain.PlayerRequestMapper

class PlayEpisode(
    private val myLibraryService: MyLibraryService,
    private val requestMapper: PlayerRequestMapper<EpisodeModel>,
    private val navigationController: NavigationController,
    private val playerNavigationDestination: NavigationDestination<PlayerNavigationLocation>
) {
    suspend operator fun invoke(episodeId: Long, libraryView: NavigationOrigin<*>) {
        val episodeModel = myLibraryService.getEpisode(episodeId)
        navigationController.navigate(
            libraryView,
            playerNavigationDestination,
            PlayerNavigationParams(requestMapper.createMediaId(episodeModel))
        )
    }
}