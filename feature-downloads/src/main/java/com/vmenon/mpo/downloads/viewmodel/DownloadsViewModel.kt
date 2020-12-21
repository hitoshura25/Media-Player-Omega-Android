package com.vmenon.mpo.downloads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    lateinit var downloadsInteractors: DownloadsInteractors

    init {
        println("DownloadViewModel()")
    }

    val downloads =
        liveData<ResultState<List<QueuedDownloadModel>>> {
            emitSource(
                downloadsInteractors.queuedDownloads().asLiveData()
            )
        }
}