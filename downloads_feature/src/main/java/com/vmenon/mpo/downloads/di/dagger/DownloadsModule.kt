package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import com.vmenon.mpo.downloads.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.downloads.usecases.RetryDownloads
import com.vmenon.mpo.my_library.domain.MyLibraryService
import dagger.Module
import dagger.Provides

@Module
object DownloadsModule {
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