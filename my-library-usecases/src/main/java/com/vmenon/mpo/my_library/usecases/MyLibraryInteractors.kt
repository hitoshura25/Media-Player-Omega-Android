package com.vmenon.mpo.my_library.usecases

data class MyLibraryInteractors(
    val getAllEpisodes: GetAllEpisodes,
    val getEpisodeDetails: GetEpisodeDetails,
    val updateAllShows: UpdateAllShows,
    val getSubscribedShows: GetSubscribedShows
)