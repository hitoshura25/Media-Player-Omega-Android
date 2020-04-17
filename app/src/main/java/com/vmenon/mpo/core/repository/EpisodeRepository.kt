package com.vmenon.mpo.core.repository

import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.model.EpisodeAndShowModel
import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable
import io.reactivex.Single

class EpisodeRepository(private val episodeDao: EpisodeDao) {
    fun save(episode: EpisodeModel): Single<EpisodeModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (episode.episodeId == 0L) {
                episode.copy(episodeId = episodeDao.insert(episode))
            } else {
                episodeDao.update(episode)
                episode
            }
        )
    }

    fun getAllEpisodes(): Flowable<List<EpisodeAndShowModel>> = episodeDao.load()

    fun getEpisodeWithShow(id: Long): Flowable<EpisodeAndShowModel> = episodeDao.byIdWithShow(id)
}