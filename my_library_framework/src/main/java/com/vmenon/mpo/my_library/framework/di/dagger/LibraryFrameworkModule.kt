package com.vmenon.mpo.my_library.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.data.MyLibraryRepository
import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.framework.MpoRetrofitApiShowUpdateDataSource
import com.vmenon.mpo.my_library.framework.RoomEpisodePersistenceDataSource
import com.vmenon.mpo.my_library.framework.RoomShowPersistenceDataSource
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
        commonFrameworkComponent: CommonFrameworkComponent
    ): ShowPersistenceDataSource =
        RoomShowPersistenceDataSource(commonFrameworkComponent.persistenceComponent().showDao())

    @Provides
    fun provideEpisodePersistenceDataSource(
        commonFrameworkComponent: CommonFrameworkComponent
    ): EpisodePersistenceDataSource =
        RoomEpisodePersistenceDataSource(
            commonFrameworkComponent.persistenceComponent().episodeDao()
        )

    @Provides
    fun provideShowUpdateDataSource(
        commonFrameworkComponent: CommonFrameworkComponent
    ): ShowUpdateDataSource = MpoRetrofitApiShowUpdateDataSource(commonFrameworkComponent.api())

}