package com.vmenon.mpo.core.repository

import com.vmenon.mpo.core.persistence.DownloadDao
import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.Completable

class DownloadRepository(private val downloadDao: DownloadDao) {
    fun getAllDownloads(): Flowable<List<DownloadModel>> = downloadDao.load()

    fun save(downloadModel: DownloadModel): Single<DownloadModel> = Single.create { emitter ->
        emitter.onSuccess(
            if (downloadModel.id == 0L) {
                downloadModel.copy(id = downloadDao.insert(downloadModel))
            } else {
                downloadDao.update(downloadModel)
                downloadModel
            }
        )
    }

    fun deleteDownload(downloadId: Long): Completable = Completable.create { emitter ->
        downloadDao.delete(downloadId)
        emitter.onComplete()
    }
}