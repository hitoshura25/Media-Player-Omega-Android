package com.vmenon.mpo.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.URLUtil
import com.vmenon.mpo.core.persistence.DownloadRepository

import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.event.DownloadUpdateEvent
import com.vmenon.mpo.model.DownloadListItem
import com.vmenon.mpo.model.DownloadModel
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.SubscribedShowModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.*

class DownloadManager(
    private val context: Context,
    private val mpoRepository: MPORepository,
    private val downloadRepository: DownloadRepository
) {

    private val handler: Handler
    private val workQueue: BlockingQueue<Runnable>
    private val threadPoolExecutor: ThreadPoolExecutor
    private val discExecutor = Executors.newSingleThreadExecutor()
    private val currentDownloads = ConcurrentHashMap<String, DownloadListItem>()
    private val downloadLiveData = MutableLiveData<List<DownloadListItem>>()

    val downloads: LiveData<List<DownloadListItem>>
        get() = downloadLiveData

    private val subscriptions = CompositeDisposable()

    init {
        workQueue = LinkedBlockingQueue()
        threadPoolExecutor = ThreadPoolExecutor(
            NUMBER_CORES, NUMBER_CORES, KEEP_ALIVE_TIME.toLong(),
            KEEP_ALIVE_TIME_UNIT, workQueue
        )
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    STATE_UPDATE -> {
                        val event = msg.obj as DownloadUpdateEvent
                        Log.d("MPO", "got update: " + event.download.progress)
                        updateLiveData()
                    }
                    else -> super.handleMessage(msg)
                }
            }
        }
    }

    fun queueDownload(download: DownloadModel) {
        subscriptions.add(downloadRepository.save(download)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { downloadId ->

            }
        )
        mpoRepository.fetchShow(
            download.showId,
            object : MPORepository.DataHandler<SubscribedShowModel> {
                override fun onDataReady(data: SubscribedShowModel) {
                    val show = data
                    mpoRepository.fetchEpisode(
                        download.episodeId,
                        object : MPORepository.DataHandler<EpisodeModel> {
                            override fun onDataReady(data: EpisodeModel) {
                                val episode = data
                                startDownload(show, episode, download)
                                Log.d("MPO", "Queued download: " + episode.downloadUrl)
                            }
                        })
                }
            })
    }

    private fun startDownload(
        show: SubscribedShowModel,
        episode: EpisodeModel,
        download: DownloadModel
    ) {
        threadPoolExecutor.submit {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            currentDownloads[episode.downloadUrl] = DownloadListItem(download, episode)

            var input: InputStream? = null
            var output: OutputStream? = null
            val filename = URLUtil.guessFileName(episode.downloadUrl, null, null)
            val showDir = File(context.filesDir, show.show.name)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            episode.filename = episodeFile.path
            mpoRepository.save(episode).blockingGet()

            try {
                val url = URL(episode.downloadUrl)
                val connection = url.openConnection()
                connection.connect()
                download.total = connection.contentLength
                input = BufferedInputStream(connection.getInputStream())
                output = FileOutputStream(episodeFile)

                val data = ByteArray(1024)
                var count: Int
                var lastPost: Long = 0

                do {
                    count = input.read(data)
                    if (count != -1) {
                        download.addProgress(count)
                        output.write(data, 0, count)
                        val downloadUpdateEvent = DownloadUpdateEvent(download)
                        Log.d("MPO", "Progress: " + download.progress + "/" + download.total)

                        // only post message every so often to avoid too many ui updates
                        if (System.currentTimeMillis() - lastPost > 3000) {
                            val completeMessage =
                                handler.obtainMessage(STATE_UPDATE, downloadUpdateEvent)
                            completeMessage.sendToTarget()
                            lastPost = System.currentTimeMillis()
                        }
                    }
                } while (count != -1)

                output.flush()
            } catch (e: Exception) {
                Log.e("MPO", "Error downloading file: " + episode.downloadUrl, e)
            } finally {
                if (output != null) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        Log.w("MPO", "Error closing output", e)
                    }

                }

                if (input != null) {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        Log.w("MPO", "Error closing input", e)
                    }

                }
            }

            currentDownloads.remove(episode.downloadUrl)
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        downloadLiveData.postValue(ArrayList(currentDownloads.values))
    }

    companion object {
        private val NUMBER_CORES = Runtime.getRuntime().availableProcessors()
        private const val KEEP_ALIVE_TIME = 1
        private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
        private const val STATE_UPDATE = 1
    }
}
