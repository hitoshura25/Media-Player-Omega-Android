package com.vmenon.mpo.downloads.framework

import com.vmenon.mpo.downloads.data.DownloadsPersistenceDataSource
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.persistence.room.entity.*

class RoomDownloadsPersistenceDataSource(private val downloadDao: DownloadDao) :
    DownloadsPersistenceDataSource {
    override suspend fun insertOrUpdate(download: DownloadModel): DownloadModel =
        downloadDao.insertOrUpdate(download.toEntity()).toModel(download.episode)

    override suspend fun getAll(): List<DownloadModel> =
        downloadDao.getAllWithShowAndEpisodeDetails().map { download -> download.toModel() }

    override suspend fun getByQueueId(queueId: Long): DownloadModel =
        downloadDao.getWithShowAndEpisodeDetailsByDownloadManagerId(queueId).toModel()

    override suspend fun delete(id: Long) {
        downloadDao.delete(id)
    }

    private fun DownloadWithShowAndEpisodeDetailsEntity.toModel() = DownloadModel(
        id = download.downloadId,
        episode = episode.toModel(
            episodeId = download.episodeId,
            show = show.toModel(download.showId)
        ),
        downloadManagerId = download.details.downloadManagerId
    )

    private fun DownloadModel.toEntity() = DownloadEntity(
        showId = episode.show.id,
        episodeId = episode.id,
        details = DownloadDetailsEntity(
            downloadManagerId = downloadManagerId
        ),
        downloadId = id
    )

    private fun DownloadEntity.toModel(episode: EpisodeModel) = DownloadModel(
        id = downloadId,
        downloadManagerId = details.downloadManagerId,
        episode = episode
    )

    private fun EpisodeDetailsEntity.toModel(episodeId: Long, show: ShowModel) = EpisodeModel(
        id = episodeId,
        description = description,
        artworkUrl = episodeArtworkUrl,
        name = episodeName,
        type = type,
        published = published,
        length = length,
        downloadUrl = downloadUrl,
        filename = filename,
        show = show
    )

    private fun ShowDetailsEntity.toModel(showId: Long) = ShowModel(
        id = showId,
        name = showName,
        artworkUrl = showArtworkUrl,
        lastUpdate = lastUpdate,
        lastEpisodePublished = lastEpisodePublished,
        isSubscribed = isSubscribed,
        author = author,
        feedUrl = feedUrl,
        genres = genres,
        description = showDescription
    )
}