package com.vmenon.mpo.repository

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.persistence.ShowPersistence

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*

class ShowRepository(
    private val showPersistence: ShowPersistence,
    private val api: MediaPlayerOmegaApi
) {
    fun getSubscribed(): Flowable<List<ShowModel>> = showPersistence.getSubscribed()

    fun save(show: ShowModel): Single<ShowModel> = Single.fromCallable {
        var showToSave: ShowModel = show
        if (show.id == 0L) {
            val existingShow = showPersistence.getByName(show.name).blockingGet()
            if (existingShow != null) {
                showToSave = existingShow.copy(isSubscribed = show.isSubscribed)
            }
        }

        showPersistence.insertOrUpdate(showToSave)
    }

    fun getSubscribedAndLastUpdatedBefore(interval: Long): Maybe<List<ShowModel>> {
        val compareTime = Date().time - interval
        return showPersistence.getSubscribedAndLastUpdatedBefore(compareTime)
    }

    fun getShowUpdate(show: ShowModel): Maybe<ShowUpdateModel> =
        api.getPodcastUpdate(show.feedUrl, show.lastEpisodePublished).map {
            ShowUpdateModel(
                newEpisode = it.toModel(show)
            )
        }
}