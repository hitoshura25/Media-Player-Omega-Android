package com.vmenon.mpo.my_library.usecases

import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationSource
import com.vmenon.mpo.player.domain.PlayerNavigationDestination
import com.vmenon.mpo.player.domain.PlayerNavigationParams
import com.vmenon.mpo.player.domain.PlayerNavigationRequest
import com.vmenon.mpo.player.domain.PlayerRequestMapper

class PlayEpisode(
    private val myLibraryService: MyLibraryService,
    private val requestMapper: PlayerRequestMapper<EpisodeModel>,
    private val navigationController: NavigationController,
    private val playerNavigationDestination: PlayerNavigationDestination
) {
    suspend operator fun invoke(episodeId: Long, libraryView: NavigationSource<*>) {
        val episodeModel = myLibraryService.getEpisode(episodeId)
        navigationController.onNavigationSelected(
            PlayerNavigationRequest(
                playerNavigationDestination,
                PlayerNavigationParams(requestMapper.createMediaId(episodeModel))
            ),
            libraryView
        )
    }
}