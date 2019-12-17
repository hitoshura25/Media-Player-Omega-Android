package com.vmenon.mpo.core.persistence

import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable
import io.reactivex.Single

class DownloadRepository(private val downloadDao: DownloadDao) {
    fun getAllDownloads(): Flowable<List<DownloadModel>> = downloadDao.load()

    fun save(downloadModel: DownloadModel): Single<Long> = Single.create { emitter ->
        emitter.onSuccess(downloadDao.save(downloadModel))
    }
}