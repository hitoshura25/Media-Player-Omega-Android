package com.vmenon.mpo.di

import com.vmenon.mpo.core.usecases.Interactors
import com.vmenon.mpo.core.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.core.usecases.RetryDownloads
import com.vmenon.mpo.core.usecases.UpdateAllShows
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.system.domain.Logger

import dagger.Module
import dagger.Provides

@Module
object AppModule {
    @Provides
    @AppScope
    fun provideInteractors(
        downloadsService: DownloadsService,
        myLibraryService: MyLibraryService,
        logger: Logger
    ): Interactors =
        Interactors(
            UpdateAllShows(myLibraryService, downloadsService, logger),
            RetryDownloads(downloadsService, 3, logger),
            NotifyDownloadCompleted(downloadsService, myLibraryService)
        )
}
