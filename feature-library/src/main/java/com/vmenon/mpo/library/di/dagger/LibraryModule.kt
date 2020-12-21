package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.data.MyLibraryRepository
import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.framework.MpoRetrofitApiShowUpdateDataSource
import com.vmenon.mpo.my_library.framework.RoomEpisodePersistenceDataSource
import com.vmenon.mpo.my_library.framework.RoomShowPersistenceDataSource
import com.vmenon.mpo.my_library.usecases.GetAllEpisodes
import com.vmenon.mpo.my_library.usecases.GetEpisodeDetails
import com.vmenon.mpo.my_library.usecases.MyLibraryInteractors
import com.vmenon.mpo.my_library.usecases.UpdateAllShows
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import dagger.Module
import dagger.Provides

@Module
class LibraryModule {
    @Provides
    fun provideLibraryService(
        showPersistenceDataSource: ShowPersistenceDataSource,
        episodePersistenceDataSource: EpisodePersistenceDataSource,
        showUpdateDataSource: ShowUpdateDataSource
    ): MyLibraryService = MyLibraryRepository(
        showPersistenceDataSource,
        episodePersistenceDataSource,
        showUpdateDataSource
    )

    @Provides
    fun provideShowPersistenceDataSource(showDao: ShowDao): ShowPersistenceDataSource =
        RoomShowPersistenceDataSource(showDao)

    @Provides
    fun provideEpisodePersistenceDataSource(episodeDao: EpisodeDao): EpisodePersistenceDataSource =
        RoomEpisodePersistenceDataSource(episodeDao)

    @Provides
    fun provideShowUpdateDataSource(
        mediaPlayerOmegaRetrofitService: MediaPlayerOmegaRetrofitService
    ): ShowUpdateDataSource = MpoRetrofitApiShowUpdateDataSource(mediaPlayerOmegaRetrofitService)

    @Provides
    fun provideMyLibraryInteractors(
        myLibraryService: MyLibraryService,
        downloadService: DownloadsService
    ): MyLibraryInteractors =
        MyLibraryInteractors(
            GetAllEpisodes(myLibraryService),
            GetEpisodeDetails(myLibraryService),
            UpdateAllShows(myLibraryService, downloadService)
        )
}