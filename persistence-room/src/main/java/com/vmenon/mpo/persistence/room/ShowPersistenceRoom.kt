package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.shows.persistence.ShowPersistence
import com.vmenon.mpo.persistence.room.dao.ShowDao
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowPersistenceRoom(private val showDao: ShowDao) : ShowPersistence {
    override fun getByName(name: String): Maybe<ShowModel> = showDao.getByName(name).map {
        it.toModel()
    }

    override fun getSubscribed(): Flow<List<ShowModel>> = showDao.getSubscribed().map { shows ->
        shows.map { it.toModel() }
    }
    override suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long) =
        showDao.getSubscribedAndLastUpdatedBefore(comparisonTime)?.map { show ->
            show.toModel()
        }

    override fun insertOrUpdate(model: ShowModel): ShowModel =
        showDao.insertOrUpdate(model.toEntity()).toModel()
}