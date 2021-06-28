package com.vmenon.mpo.di

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.core.usecases.Interactors
import com.vmenon.mpo.core.usecases.NotifyDownloadCompleted
import com.vmenon.mpo.core.usecases.RetryDownloads
import com.vmenon.mpo.core.usecases.UpdateAllShows
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.domain.MyLibraryService

import dagger.Module
import dagger.Provides

@Module
object AppModule {
    @Provides
    fun provideInteractors(
        downloadsService: DownloadsService,
        myLibraryService: MyLibraryService,
        system: System
    ): Interactors =
        Interactors(
            UpdateAllShows(myLibraryService, downloadsService, system),
            RetryDownloads(downloadsService, 3, system),
            NotifyDownloadCompleted(downloadsService, myLibraryService)
        )
}
