package com.vmenon.mpo.downloads.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    lateinit var downloadsInteractors: DownloadsInteractors

    val downloads = liveData {
        emitSource(
            downloadsInteractors.queuedDownloads().asLiveData()
        )
    }
}