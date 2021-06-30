package com.vmenon.mpo.my_library.data

import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.my_library.domain.ShowUpdateModel

interface ShowUpdateDataSource {
    suspend fun getShowUpdate(show: ShowModel): ShowUpdateModel?
}