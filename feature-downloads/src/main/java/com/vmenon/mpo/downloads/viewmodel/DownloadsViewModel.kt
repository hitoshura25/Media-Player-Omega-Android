package com.vmenon.mpo.downloads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.vmenon.mpo.downloads.repository.DownloadRepository
import com.vmenon.mpo.rx.scheduler.SchedulerProvider
import kotlinx.coroutines.delay
import javax.inject.Inject

class DownloadsViewModel : ViewModel() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

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