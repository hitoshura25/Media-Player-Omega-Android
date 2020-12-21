package com.vmenon.mpo.downloads.domain

import com.vmenon.mpo.my_library.domain.EpisodeModel

data class DownloadModel(
    val id: Long = 0L,
    val episode: EpisodeModel, // Maybe decouple this from my-library-domain
    val downloadManagerId: Long
)