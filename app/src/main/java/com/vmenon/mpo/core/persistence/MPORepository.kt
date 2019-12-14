package com.vmenon.mpo.core.persistence

import androidx.lifecycle.LiveData
import android.os.Handler
import android.os.Looper

import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.service.MediaPlayerOmegaService

import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MPORepository(
    private val service: MediaPlayerOmegaService,
    private val showDao: ShowDao,
    private val episodeDao: EpisodeDao
) {
    private val discExecutor: Executor
    private val mainThreadExecutor: Executor

    val allShows: LiveData<List<Show>>
        get() = showDao.load()

    val allEpisodes: LiveData<List<Episode>>
        get() = episodeDao.load()

    // Listener used to signal data being fetched. Workaround for cases where LiveData cannot be used
    // i.e., in MediaBrowserService instances as they don't extend LifeCycleService
    interface DataHandler<T> {
        fun onDataReady(data: T)
    }

    init {
        this.discExecutor = Executors.newSingleThreadExecutor()
        this.mainThreadExecutor = MainThreadExecutor()
    }

    fun getLiveShow(id: Long): LiveData<Show> {
        return showDao.getLiveById(id)
    }

    fun fetchShow(id: Long, dataHandler: DataHandler<Show>) {
        // TODO: Caching
        discExecutor.execute {
            val show = showDao.getById(id)
            mainThreadExecutor.execute { dataHandler.onDataReady(show) }
        }
    }

    fun save(show: Show) {
        discExecutor.execute { showDao.save(show) }
    }

    fun notUpdatedInLast(interval: Long): List<Show> {
        val compareTime = Date().time - interval
        return showDao.loadLastUpdatedBefore(compareTime)
    }

    fun getLiveEpisode(id: Long): LiveData<Episode> {
        // TODO: Cache
        return episodeDao.liveById(id)
    }

    fun fetchEpisode(id: Long, dataHandler: DataHandler<Episode>) {
        // TODO: Implement cache
        discExecutor.execute {
            val episode = episodeDao.byId(id)
            mainThreadExecutor.execute { dataHandler.onDataReady(episode) }
        }
    }

    fun save(episode: Episode) {
        discExecutor.execute { episodeDao.save(episode) }
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
