package com.vmenon.mpo.my_library.usecases

data class MyLibraryInteractors(
    val getAllEpisodes: GetAllEpisodes,
    val getEpisodeDetails: GetEpisodeDetails,
    val getSubscribedShows: GetSubscribedShows,
    val playEpisode: PlayEpisode,
    val searchForShows: SearchForShows
)