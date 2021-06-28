package com.vmenon.mpo.core.usecases

import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService

class NotifyDownloadCompleted(
    private val downloadsService: DownloadsService,
    private val myLibraryService: MyLibraryService
) {
    suspend operator fun invoke(downloadQueueId: Long) {
        val completedDownload = downloadsService.getCompletedDownloadByQueueId(downloadQueueId)
        when (completedDownload.download.downloadRequestType) {
            DownloadRequestType.EPISODE -> {
                val episode = myLibraryService.getEpisode(completedDownload.download.requesterId)
                myLibraryService.saveEpisode(
                    episode.copy(filename = completedDownload.pathToFile)
                )
            }
            else -> {}
        }
        downloadsService.delete(completedDownload.download.id)
    }
}