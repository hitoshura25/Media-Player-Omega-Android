package com.vmenon.mpo.my_library.domain

interface MyLibraryService {
    suspend fun saveShow(show: ShowModel): ShowModel
    suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel?
    suspend fun saveEpisode(episodeModel: EpisodeModel): EpisodeModel
    suspend fun getShowByName(name: String): ShowModel?
    suspend fun getEpisodeByName(name: String): EpisodeModel?
    suspend fun getAllEpisodes(): List<EpisodeModel>
    suspend fun getEpisode(episodeId: Long): EpisodeModel
}