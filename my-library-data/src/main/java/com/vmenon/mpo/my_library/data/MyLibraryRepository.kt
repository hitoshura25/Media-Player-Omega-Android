package com.vmenon.mpo.my_library.data

import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel

class MyLibraryRepository(
    private val showPersistenceDataSource: ShowPersistenceDataSource,
    private val episodePersistenceDataSource: EpisodePersistenceDataSource,
    private val showUpdateDataSource: ShowUpdateDataSource
) : MyLibraryService {
    override suspend fun saveShow(show: ShowModel): ShowModel {
        var showToSave: ShowModel = show
        if (show.id == 0L) {
            val existingShow = showPersistenceDataSource.getByName(show.name)
            if (existingShow != null) {
                showToSave = existingShow.copy(isSubscribed = show.isSubscribed)
            }
        }

        return showPersistenceDataSource.insertOrUpdate(showToSave)
    }

    override suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel? =
        showUpdateDataSource.getShowUpdate(show)

    override suspend fun saveEpisode(episodeModel: EpisodeModel) =
        episodePersistenceDataSource.insertOrUpdate(episodeModel)

    override suspend fun getShowByName(name: String): ShowModel? =
        showPersistenceDataSource.getByName(name)

    override suspend fun getEpisodeByName(name: String): EpisodeModel? =
        episodePersistenceDataSource.getByName(name)

    override suspend fun getAllEpisodes(): List<EpisodeModel> =
        episodePersistenceDataSource.getAll()

    override suspend fun getEpisode(episodeId: Long): EpisodeModel =
        episodePersistenceDataSource.getEpisode(episodeId)

    override suspend fun getShowsSubscribedAndLastUpdatedBefore(interval: Long): List<ShowModel>? =
        showPersistenceDataSource.getSubscribedAndLastUpdatedBefore(interval)
}