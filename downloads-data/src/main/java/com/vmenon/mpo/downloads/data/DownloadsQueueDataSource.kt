package com.vmenon.mpo.downloads.data

import com.vmenon.mpo.my_library.domain.EpisodeModel

interface DownloadsQueueDataSource {
    suspend fun getAllQueued(queueIds:Collection<Long>): List<DownloadQueueItem>
    suspend fun queueDownloadAndGetQueueId(episode: EpisodeModel): Long
}