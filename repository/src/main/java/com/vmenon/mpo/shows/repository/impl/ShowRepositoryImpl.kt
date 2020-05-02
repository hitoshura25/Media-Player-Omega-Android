package com.vmenon.mpo.shows.repository.impl

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.repository.toModel
import com.vmenon.mpo.shows.repository.ShowRepository

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*

class ShowRepositoryImpl(
    private val showPersistence: com.vmenon.mpo.shows.persistence.ShowPersistence,
    private val api: MediaPlayerOmegaApi
) : ShowRepository {
    override fun getSubscribed(): Flowable<List<ShowModel>> = showPersistence.getSubscribed()

    override fun save(show: ShowModel): Single<ShowModel> = Single.fromCallable {
        var showToSave: ShowModel = show
        if (show.id == 0L) {
            val existingShow = showPersistence.getByName(show.name).blockingGet()
            if (existingShow != null) {
                showToSave = existingShow.copy(isSubscribed = show.isSubscribed)
            }
        }

        showPersistence.insertOrUpdate(showToSave)
    }

    override fun getSubscribedAndLastUpdatedBefore(interval: Long): Maybe<List<ShowModel>> {
        val compareTime = Date().time - interval
        return showPersistence.getSubscribedAndLastUpdatedBefore(compareTime)
    }

    override fun getShowUpdate(show: ShowModel): Maybe<ShowUpdateModel> =
        api.getPodcastUpdate(show.feedUrl, show.lastEpisodePublished).map {
            ShowUpdateModel(
                newEpisode = it.toModel(show)
            )
        }
}