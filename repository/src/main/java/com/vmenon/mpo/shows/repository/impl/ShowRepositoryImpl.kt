package com.vmenon.mpo.shows.repository.impl

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.model.ShowUpdateModel
import com.vmenon.mpo.api.MediaPlayerOmegaApi
import com.vmenon.mpo.repository.toModel
import com.vmenon.mpo.shows.repository.ShowRepository

import kotlinx.coroutines.flow.Flow
import java.util.*

class ShowRepositoryImpl(
    private val showPersistence: com.vmenon.mpo.shows.persistence.ShowPersistence,
    private val api: MediaPlayerOmegaApi
) : ShowRepository {
    override fun getSubscribed(): Flow<List<ShowModel>> = showPersistence.getSubscribed()

    override suspend fun save(show: ShowModel): ShowModel {
        var showToSave: ShowModel = show
        if (show.id == 0L) {
            val existingShow = showPersistence.getByName(show.name).blockingGet()
            if (existingShow != null) {
                showToSave = existingShow.copy(isSubscribed = show.isSubscribed)
            }
        }

        return showPersistence.insertOrUpdate(showToSave)
    }

    override suspend fun getSubscribedAndLastUpdatedBefore(interval: Long): List<ShowModel>? {
        val compareTime = Date().time - interval
        return showPersistence.getSubscribedAndLastUpdatedBefore(compareTime)
    }

    override suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel? =
        api.getPodcastUpdate(show.feedUrl, show.lastEpisodePublished).map {
            ShowUpdateModel(
                newEpisode = it.toModel(show)
            )
        }.blockingGet()
}