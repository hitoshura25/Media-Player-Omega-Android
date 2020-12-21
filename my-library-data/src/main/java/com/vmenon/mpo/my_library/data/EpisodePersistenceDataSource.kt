package com.vmenon.mpo.my_library.data

import com.vmenon.mpo.my_library.domain.EpisodeModel

interface EpisodePersistenceDataSource {
    suspend fun insertOrUpdate(episode: EpisodeModel): EpisodeModel
    suspend fun getByName(name: String): EpisodeModel?

}