package com.vmenon.mpo.downloads.usecases

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService

class NotifyDownloadCompleted(
    private val downloadsService: DownloadsService,
    private val myLibraryService: MyLibraryService
) {
    suspend operator fun invoke(downloadQueueId: Long) {
        val completedDownload = downloadsService.getCompletedDownloadByQueueId(downloadQueueId)
        myLibraryService.saveEpisode(
            completedDownload.download.episode.copy(filename = completedDownload.pathToFile)
        )
        downloadsService.delete(completedDownload.download.id)
    }
}