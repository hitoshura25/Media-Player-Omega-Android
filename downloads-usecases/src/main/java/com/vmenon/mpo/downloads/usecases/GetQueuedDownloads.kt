package com.vmenon.mpo.downloads.usecases

import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.ResultState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.domain.QueuedDownloadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetQueuedDownloads(
    private val downloadsService: DownloadsService,
    private val system: System
) {
    suspend operator fun invoke(): Flow<ResultState<List<QueuedDownloadModel>>> = flow {
        emit(LoadingState)
        while (true) {
            emit(SuccessState(downloadsService.getAllQueued()))
            system.delay(2000L)
        }
    }
}