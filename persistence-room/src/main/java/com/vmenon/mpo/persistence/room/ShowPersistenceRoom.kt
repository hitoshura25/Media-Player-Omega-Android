package com.vmenon.mpo.persistence.room

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.shows.persistence.ShowPersistence
import com.vmenon.mpo.persistence.room.dao.ShowDao
import io.reactivex.Flowable
import io.reactivex.Maybe

class ShowPersistenceRoom(private val showDao: ShowDao) : ShowPersistence {
    override fun getByName(name: String): Maybe<ShowModel> = showDao.getByName(name).map {
        it.toModel()
    }

    override fun getSubscribed(): Flowable<List<ShowModel>> = showDao.getSubscribed().map { shows ->
        shows.map { it.toModel() }
    }
    override fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long) =
        showDao.getSubscribedAndLastUpdatedBefore(comparisonTime).map { shows ->
            shows.map { it.toModel() }
        }

    override fun insertOrUpdate(model: ShowModel): ShowModel =
        showDao.insertOrUpdate(model.toEntity()).toModel()
}