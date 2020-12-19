package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.shows.persistence.ShowPersistence
import com.vmenon.mpo.persistence.room.dao.ShowDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowPersistenceRoom(private val showDao: ShowDao) : ShowPersistence {
    override suspend fun getByName(name: String): ShowModel? = showDao.getByName(name)?.toModel()

    override suspend fun getSubscribed(): List<ShowModel> = showDao.getSubscribed().map { it.toModel() }

    override suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long) =
        showDao.getSubscribedAndLastUpdatedBefore(comparisonTime).map { show ->
            show.toModel()
        }

    override fun insertOrUpdate(model: ShowModel): ShowModel =
        showDao.insertOrUpdate(model.toEntity()).toModel()
}