package com.vmenon.mpo.downloads.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.downloads.data.DownloadsRepository
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.framework.DownloadManagerDownloadQueueDataSource
import com.vmenon.mpo.downloads.framework.FileSystemMediaPersistenceDataSource
import com.vmenon.mpo.downloads.framework.RoomDownloadsPersistenceDataSource
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import com.vmenon.mpo.system.domain.Logger
import dagger.Module
import dagger.Provides

@Module
object DownloadsFrameworkModule {
    @Provides
    fun provideDownloadsService(
        application: Application,
        logger: Logger,
        downloadDao: DownloadDao
    ): DownloadsService =
        DownloadsRepository(
            DownloadManagerDownloadQueueDataSource(
                application
            ),
            RoomDownloadsPersistenceDataSource(
                downloadDao,
                logger
            ),
            FileSystemMediaPersistenceDataSource(
                application
            )
        )
}