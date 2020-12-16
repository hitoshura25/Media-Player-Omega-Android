package com.vmenon.mpo.downloads.repository

import com.vmenon.mpo.model.*
import io.reactivex.Flowable

interface DownloadRepository {
    fun getAllQueued(): Flowable<List<QueuedDownloadModel>>
    suspend fun queueDownload(episode: EpisodeModel): DownloadModel
    suspend fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): DownloadModel

    suspend fun notifyDownloadCompleted(downloadManagerId: Long)
}