package com.vmenon.mpo.downloads.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.downloads.data.DownloadsRepository
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.framework.DownloadManagerDownloadQueueDataSource
import com.vmenon.mpo.downloads.framework.FileSystemMediaPersistenceDataSource
import com.vmenon.mpo.downloads.framework.RoomDownloadsPersistenceDataSource
import dagger.Module
import dagger.Provides

@Module
object DownloadsFrameworkModule {
    @Provides
    fun provideDownloadsService(
        commonFrameworkComponent: CommonFrameworkComponent
    ): DownloadsService =
        DownloadsRepository(
            DownloadManagerDownloadQueueDataSource(
                commonFrameworkComponent.systemFrameworkComponent().application()
            ),
            RoomDownloadsPersistenceDataSource(
                commonFrameworkComponent.persistenceComponent().downloadDao(),
                commonFrameworkComponent.systemFrameworkComponent().system()
            ),
            FileSystemMediaPersistenceDataSource(
                commonFrameworkComponent.systemFrameworkComponent().application()
            )
        )
}