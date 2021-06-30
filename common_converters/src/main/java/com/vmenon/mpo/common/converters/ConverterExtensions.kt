package com.vmenon.mpo.common.converters

import com.vmenon.mpo.downloads.domain.DownloadRequest
import com.vmenon.mpo.downloads.domain.DownloadRequestType
import com.vmenon.mpo.my_library.domain.EpisodeModel

fun EpisodeModel.toDownloadRequest(): DownloadRequest =
    DownloadRequest(
        name = name,
        downloadRequestType = DownloadRequestType.EPISODE,
        downloadUrl = downloadUrl,
        imageUrl = artworkUrl ?: show.artworkUrl,
        requesterId = id
    )