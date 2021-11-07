package com.vmenon.mpo.downloads.di.dagger

import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.downloads.usecases.DownloadsInteractors
import com.vmenon.mpo.downloads.usecases.GetQueuedDownloads
import com.vmenon.mpo.system.domain.ThreadUtil
import dagger.Module
import dagger.Provides

@Module
object DownloadsModule {
    @Provides
    @DownloadsScope
    fun provideDownloadsInteractors(
        downloadsService: DownloadsService,
        threadUtil: ThreadUtil
    ): DownloadsInteractors =
        DownloadsInteractors(
            GetQueuedDownloads(downloadsService, threadUtil)
        )
}