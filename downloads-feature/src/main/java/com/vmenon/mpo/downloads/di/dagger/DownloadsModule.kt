package com.vmenon.mpo.downloads.di.dagger

import android.app.Application
import androidx.fragment.app.Fragment
import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.data.DownloadsRepository
import com.vmenon.mpo.downloads.domain.DownloadsDestination
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.framework.DownloadManagerDownloadQueueDataSource
import com.vmenon.mpo.downloads.framework.FileSystemMediaPersistenceDataSource
import com.vmenon.mpo.downloads.framework.RoomDownloadsPersistenceDataSource
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import com.vmenon.mpo.downloads.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.navigation.framework.FragmentDestination
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
            RoomDownloadsPersistenceDataSource(downloadsDao),
            FileSystemMediaPersistenceDataSource(application)
        )

    @Provides
    fun provideDownloadsInteractors(
        downloadsService: DownloadsService,
        myLibraryService: MyLibraryService
    ): DownloadsInteractors =
        DownloadsInteractors(
            GetQueuedDownloads(downloadsService),
            NotifyDownloadCompleted(downloadsService, myLibraryService)
        )

    @Provides
    fun provideDownloadsNavigationDestination(): DownloadsDestination =
        object : FragmentDestination, DownloadsDestination {
            override val fragmentCreator: () -> Fragment
                get() = { DownloadsFragment() }
            override val containerId: Int = R.id.fragmentContainerLayout
            override val tag: String
                get() = DownloadsFragment::class.java.name
        }
}