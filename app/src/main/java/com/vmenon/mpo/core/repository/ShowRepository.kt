package com.vmenon.mpo.core.repository

import com.vmenon.mpo.core.persistence.ShowDao
import com.vmenon.mpo.model.ShowModel
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*

class ShowRepository(private val showDao: ShowDao) {
    fun getAllSubscribedShows(): Flowable<List<ShowModel>> = showDao.loadAllSubscribed()

    fun getShow(id: Long): Flowable<ShowModel> = showDao.getById(id)

    fun save(show: ShowModel): Single<ShowModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (show.id == 0L) {
                val existingShow = showDao.getByName(show.showDetails.name).blockingGet()
                if (existingShow != null) {
                    existingShow.isSubscribed = show.isSubscribed
                    showDao.update(existingShow)
                    existingShow
                } else {
                    show.copy(id = showDao.insert(show))
                }
            } else {
                showDao.update(show)
                show
            }
        )
    }

    fun notUpdatedInLast(interval: Long): Maybe<List<ShowModel>> {
        val compareTime = Date().time - interval
        return showDao.loadSubscribedLastUpdatedBefore(compareTime)
    }
}