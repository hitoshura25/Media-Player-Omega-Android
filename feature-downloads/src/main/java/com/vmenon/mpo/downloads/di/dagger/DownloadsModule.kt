package com.vmenon.mpo.downloads.di.dagger

import android.app.Application
import com.vmenon.mpo.downloads.data.DownloadsRepository
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.framework.DownloadManagerDownloadQueueDataSource
import com.vmenon.mpo.downloads.framework.RoomDownloadsPersistenceDataSource
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import dagger.Module
import dagger.Provides

@Module
class DownloadsModule {
    @Provides
    fun provideDownloadsService(
        application: Application,
        downloadsDao: DownloadDao
    ): DownloadsService =
        DownloadsRepository(
            DownloadManagerDownloadQueueDataSource(application),
            RoomDownloadsPersistenceDataSource(downloadsDao)
        )
}