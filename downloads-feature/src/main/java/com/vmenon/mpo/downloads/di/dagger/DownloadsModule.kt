package com.vmenon.mpo.downloads.di.dagger

import android.app.Application
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.data.DownloadsRepository
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.framework.DownloadManagerDownloadQueueDataSource
import com.vmenon.mpo.downloads.framework.FileSystemMediaPersistenceDataSource
import com.vmenon.mpo.downloads.framework.RoomDownloadsPersistenceDataSource
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import com.vmenon.mpo.downloads.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.downloads.usecases.RetryDownloads
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.persistence.room.dao.DownloadDao
import dagger.Module
import dagger.Provides

@Module
class DownloadsModule {
    @Provides
    fun provideDownloadsService(
        application: Application,
        downloadsDao: DownloadDao,
        system: System
    ): DownloadsService =
        DownloadsRepository(
            DownloadManagerDownloadQueueDataSource(application),
            RoomDownloadsPersistenceDataSource(downloadsDao, system),
            FileSystemMediaPersistenceDataSource(application)
        )

    @Provides
    fun provideDownloadsInteractors(
        downloadsService: DownloadsService,
        myLibraryService: MyLibraryService,
        system: System
    ): DownloadsInteractors =
        DownloadsInteractors(
            GetQueuedDownloads(downloadsService, system),
            NotifyDownloadCompleted(downloadsService, myLibraryService),
            RetryDownloads(downloadsService, 3, system)
        )
}