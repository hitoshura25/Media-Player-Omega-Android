package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.downloads.persistence.DownloadPersistence
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import io.reactivex.Flowable

class DownloadPersistenceRoom(private val downloadDao: DownloadDao) :
    DownloadPersistence {
    override fun getByDownloadManagerId(id: Long): Flowable<DownloadModel> =
        downloadDao.getWithShowAndEpisodeDetailsByDownloadManagerId(id).map { it.toModel() }

    override fun getAll(): Flowable<List<DownloadModel>> =
        downloadDao.getAllWithShowAndEpisodeDetails().map { downloads ->
            downloads.map { it.toModel() }
        }

    override fun delete(id: Long) {
        downloadDao.delete(id)
    }

    override fun insertOrUpdate(model: DownloadModel): DownloadModel =
        downloadDao.insertOrUpdate(model.toEntity()).toModel(model.episode)
}