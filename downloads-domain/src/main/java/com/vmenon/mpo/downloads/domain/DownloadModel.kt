package com.vmenon.mpo.downloads.domain

import com.vmenon.mpo.my_library.domain.EpisodeModel

data class DownloadModel(
    val id: Long = 0L,
    val episode: EpisodeModel,
    val downloadManagerId: Long
)