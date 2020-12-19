package com.vmenon.mpo.downloads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.downloads.repository.DownloadRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

    init {
        println("DownloadViewModel()")
    }

    val downloads = liveData{
        while (true) {
            emit(downloadRepository.getAllQueued())
            delay(2000L)
        }
    }
}