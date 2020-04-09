package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.core.SchedulerProvider
import com.vmenon.mpo.core.repository.DownloadRepository
import com.vmenon.mpo.core.repository.EpisodeRepository
import com.vmenon.mpo.model.DownloadListItem
import io.reactivex.Flowable
import javax.inject.Inject

class DownloadsViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    episodeRepository: EpisodeRepository,
    schedulerProvider: SchedulerProvider
) : ViewModel() {
    val downloads: Flowable<List<DownloadListItem>> =
        downloadRepository.getAllDownloads().map { downloads ->
            downloads.map { download ->
                DownloadListItem(
                    download,
                    episodeRepository.getEpisode(download.episodeId).blockingFirst()
                )
            }
        }.subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.main())
}