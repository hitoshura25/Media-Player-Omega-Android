package com.vmenon.mpo.my_library.data

import com.vmenon.mpo.my_library.domain.ShowModel

interface ShowPersistenceDataSource {
    suspend fun insertOrUpdate(showModel: ShowModel): ShowModel
    suspend fun getByName(name: String): ShowModel?
    suspend fun getSubscribed(): List<ShowModel>
    suspend fun getSubscribedAndLastUpdatedBefore(comparisonTime: Long): List<ShowModel>
}