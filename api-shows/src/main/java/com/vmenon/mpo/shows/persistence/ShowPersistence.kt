package com.vmenon.mpo.shows.persistence

import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.persistence.BasePersistence

interface ShowPersistence : BasePersistence<ShowModel> {
    suspend fun getByName(name: String): ShowModel?
    suspend fun getSubscribed(): List<ShowModel>
    suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): List<ShowModel>
}