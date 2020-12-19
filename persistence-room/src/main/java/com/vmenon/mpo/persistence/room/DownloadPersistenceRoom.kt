package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.downloads.persistence.DownloadPersistence
import com.vmenon.mpo.persistence.room.dao.DownloadDao

class DownloadPersistenceRoom(private val downloadDao: DownloadDao) :
    DownloadPersistence {
    override suspend fun getByDownloadManagerId(id: Long): DownloadModel =
        downloadDao.getWithShowAndEpisodeDetailsByDownloadManagerId(id).toModel()

    override suspend fun getAll(): List<DownloadModel> =
        downloadDao.getAllWithShowAndEpisodeDetails().map { it.toModel() }

    override suspend fun delete(id: Long) {
        downloadDao.delete(id)
    }

    override fun insertOrUpdate(model: DownloadModel): DownloadModel =
        downloadDao.insertOrUpdate(model.toEntity()).toModel(model.episode)
}