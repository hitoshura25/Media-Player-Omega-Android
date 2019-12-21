package com.vmenon.mpo.core.repository

import androidx.lifecycle.LiveData
import android.os.Handler
import android.os.Looper
import com.vmenon.mpo.core.persistence.EpisodeDao
import com.vmenon.mpo.core.persistence.ShowDao

import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.service.MediaPlayerOmegaService
import io.reactivex.Flowable
import io.reactivex.Single

import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MPORepository(
    private val service: MediaPlayerOmegaService,
    private val showDao: ShowDao,
    private val episodeDao: EpisodeDao
) {
    private val discExecutor = Executors.newSingleThreadExecutor()
    private val mainThreadExecutor =
        MainThreadExecutor()

    // Listener used to signal data being fetched. Workaround for cases where LiveData cannot be used
    // i.e., in MediaBrowserService instances as they don't extend LifeCycleService
    interface DataHandler<T> {
        fun onDataReady(data: T)
    }

    fun getAllSubscribedShows(): Flowable<List<ShowModel>> = showDao.loadAllSubscribed()

    fun getLiveShow(id: Long): LiveData<ShowModel> {
        return showDao.getLiveById(id)
    }

    fun fetchShow(id: Long, dataHandler: DataHandler<ShowModel>) {
        // TODO: Caching
        discExecutor.execute {
            val show = showDao.getById(id)
            mainThreadExecutor.execute { dataHandler.onDataReady(show) }
        }
    }

    fun save(show: ShowModel): Single<ShowModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (show.id == 0L) {
                val existingShow = showDao.getByName(show.showDetails.name)
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

    fun notUpdatedInLast(interval: Long): List<ShowModel> {
        val compareTime = Date().time - interval
        return showDao.loadSubscribedLastUpdatedBefore(compareTime)
    }

    fun fetchEpisode(id: Long, dataHandler: DataHandler<EpisodeModel>) {
        // TODO: Implement cache
        discExecutor.execute {
            val episode = episodeDao.byId(id).firstElement().blockingGet()
            mainThreadExecutor.execute { dataHandler.onDataReady(episode) }
        }
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
