package com.vmenon.mpo.my_library.framework.di.dagger

import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.data.MyLibraryRepository
import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.framework.MpoRetrofitApiShowUpdateDataSource
import com.vmenon.mpo.my_library.framework.RoomEpisodePersistenceDataSource
import com.vmenon.mpo.my_library.framework.RoomShowPersistenceDataSource
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import dagger.Module
import dagger.Provides

@Module
object LibraryFrameworkModule {
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
    fun provideShowPersistenceDataSource(
        showDao: ShowDao
    ): ShowPersistenceDataSource =
        RoomShowPersistenceDataSource(showDao)

    @Provides
    fun provideEpisodePersistenceDataSource(
        episodeDao: EpisodeDao
    ): EpisodePersistenceDataSource =
        RoomEpisodePersistenceDataSource(
            episodeDao
        )

    @Provides
    fun provideShowUpdateDataSource(api: MediaPlayerOmegaRetrofitService): ShowUpdateDataSource =
        MpoRetrofitApiShowUpdateDataSource(api)

}