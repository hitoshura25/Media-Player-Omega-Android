package com.vmenon.mpo.shows.persistence

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.persistence.BasePersistence
import kotlinx.coroutines.flow.Flow

interface ShowPersistence : BasePersistence<ShowModel> {
    suspend fun getByName(name: String): ShowModel?
    fun getSubscribed(): Flow<List<ShowModel>>
    suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): List<ShowModel>?
}