package com.vmenon.mpo.repository

import com.vmenom.mpo.model.ShowModel
import com.vmenom.mpo.model.ShowUpdateModel
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaService
import com.vmenon.mpo.persistence.room.dao.ShowDao

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*

class ShowRepository(
    private val showDao: ShowDao,
    private val api: MediaPlayerOmegaService
) {
    fun getSubscribed(): Flowable<List<ShowModel>> = showDao.getSubscribed().map { shows ->
        shows.map { it.toModel() }
    }

    fun save(show: ShowModel): Single<ShowModel> = Single.fromCallable {
        if (show.id == 0L) {
            val existingShow = showDao.getByName(show.name).blockingGet()
            if (existingShow != null) {
                existingShow.details.isSubscribed = show.isSubscribed
                showDao.update(existingShow)
                existingShow.toModel()
            } else {
                show.copy(id = showDao.insert(show.toEntity()))
            }
        } else {
            showDao.update(show.toEntity())
            show
        }
    }

    fun getSubscribedAndLastUpdatedBefore(interval: Long): Maybe<List<ShowModel>> {
        val compareTime = Date().time - interval
        return showDao.getSubscribedAndLastUpdatedBefore(compareTime).map { shows ->
            shows.map { it.toModel()  }
        }
    }

    fun getShowUpdate(show: ShowModel): Maybe<ShowUpdateModel> =
        api.getPodcastUpdate(show.feedUrl, show.lastEpisodePublished).map {
            ShowUpdateModel(
                newEpisode = it.toModel(show)
            )
        }
}