package com.vmenon.mpo.core.repository

import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.model.EpisodeModel
import io.reactivex.Flowable
import io.reactivex.Single

class EpisodeRepository(private val episodeDao: EpisodeDao) {
    fun save(episode: EpisodeModel): Single<EpisodeModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (episode.id == 0L) {
                episode.copy(id = episodeDao.insert(episode))
            } else {
                episodeDao.update(episode)
                episode
            }
        )
    }

    fun getEpisode(id: Long): Flowable<EpisodeModel> = episodeDao.byId(id)

    fun getAllEpisodes(): Flowable<List<EpisodeModel>> = episodeDao.load()
}