package com.vmenon.mpo.search.mvi

import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.my_library.domain.ShowModel

sealed class ShowDetailsViewEffect {
    data class ShowSubscribedViewEffect(val subscribedShow: ShowModel) : ShowDetailsViewEffect()
    data class DownloadQueuedViewEffect(val queuedDownload: DownloadModel): ShowDetailsViewEffect()
}