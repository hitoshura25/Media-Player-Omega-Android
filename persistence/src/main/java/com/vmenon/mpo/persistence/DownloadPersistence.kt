package com.vmenon.mpo.persistence

import com.vmenon.mpo.model.DownloadModel
import io.reactivex.Flowable

interface DownloadPersistence : BasePersistence<DownloadModel> {
    fun getByDownloadManagerId(id: Long): Flowable<DownloadModel>
    fun getAll(): Flowable<List<DownloadModel>>
    fun delete(id: Long)
}