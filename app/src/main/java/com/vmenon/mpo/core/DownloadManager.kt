package com.vmenon.mpo.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.URLUtil

import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.event.DownloadUpdateEvent

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DownloadManager(private val context: Context, private val mpoRepository: MPORepository) {

    private val handler: Handler
    private val workQueue: BlockingQueue<Runnable>
    private val threadPoolExecutor: ThreadPoolExecutor
    private val currentDownloads = ConcurrentHashMap<String, Download>()
    private val downloadLiveData = MutableLiveData<List<Download>>()

    val downloads: LiveData<List<Download>>
        get() = downloadLiveData

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

    fun queueDownload(download: Download) {
        val show = download.show
        val episode = download.episode
        threadPoolExecutor.submit {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            currentDownloads[episode!!.downloadUrl!!] = download

            var input: InputStream? = null
            var output: OutputStream? = null
            val filename = URLUtil.guessFileName(episode.downloadUrl, null, null)
            val showDir = File(context.filesDir, show!!.name)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            episode.filename = episodeFile.path
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
                show.lastEpisodePublished = episode.published
                episode.showId = show.id
                mpoRepository.save(episode)
                mpoRepository.save(show)
            } catch (e: Exception) {
                Log.e("MPO", "Error downloading file: " + episode.downloadUrl!!, e)
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
        Log.d("MPO", "Queued download: " + download.episode!!.downloadUrl!!)
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
