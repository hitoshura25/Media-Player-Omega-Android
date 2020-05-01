package com.vmenon.mpo.downloads.repository

import com.vmenon.mpo.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DownloadRepository {
    fun getAllQueued(): Flowable<List<QueuedDownloadModel>>
    fun queueDownload(episode: EpisodeModel): Single<DownloadModel>
    fun queueDownload(
        show: ShowSearchResultModel,
        episode: ShowSearchResultEpisodeModel
    ): Single<DownloadModel>
    fun notifyDownloadCompleted(downloadManagerId: Long): Completable
}