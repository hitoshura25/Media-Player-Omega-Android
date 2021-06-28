package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import dagger.Module
import dagger.Provides

@Module
object DownloadsModule {
    @Provides
    fun provideDownloadsInteractors(
        downloadsService: DownloadsService,
        system: System
    ): DownloadsInteractors =
        DownloadsInteractors(
            GetQueuedDownloads(downloadsService, system)
        )
}