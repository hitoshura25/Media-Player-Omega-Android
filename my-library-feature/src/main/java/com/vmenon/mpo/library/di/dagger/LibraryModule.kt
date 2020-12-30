package com.vmenon.mpo.library.di.dagger

import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.view.fragment.EpisodeDetailsFragment
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.data.MyLibraryRepository
import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.*
import com.vmenon.mpo.my_library.framework.MpoRetrofitApiShowUpdateDataSource
import com.vmenon.mpo.my_library.framework.RoomEpisodePersistenceDataSource
import com.vmenon.mpo.my_library.framework.RoomShowPersistenceDataSource
import com.vmenon.mpo.my_library.usecases.*
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.search.domain.SearchNavigationLocation
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
        downloadService: DownloadsService,
        navigationController: NavigationController,
        requestMapper: PlayerRequestMapper<EpisodeModel>,
        playerNavigationDestination: NavigationDestination<PlayerNavigationLocation>,
        searchNavigationDestination: NavigationDestination<SearchNavigationLocation>
    ): MyLibraryInteractors =
        MyLibraryInteractors(
            GetAllEpisodes(myLibraryService),
            GetEpisodeDetails(myLibraryService),
            UpdateAllShows(myLibraryService, downloadService),
            GetSubscribedShows(myLibraryService),
            PlayEpisode(
                myLibraryService,
                requestMapper,
                navigationController,
                playerNavigationDestination
            ),
            SearchForShows(navigationController, searchNavigationDestination)
        )

    @Provides
    fun provideLibraryNavigationDestination(): NavigationDestination<MyLibraryNavigationLocation> =
        FragmentDestination(
            fragmentCreator = { LibraryFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = LibraryFragment::class.java.name
        )

    @Provides
    fun provideShowsNavigationDestination(): NavigationDestination<SubscribedShowsLocation> =
        FragmentDestination(
            fragmentCreator = { SubscribedShowsFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = SubscribedShowsFragment::class.java.name
        )

    @Provides
    fun provideEpisodeDetailsDestination(): NavigationDestination<EpisodeDetailsLocation> =
        FragmentDestination(
            fragmentCreator = { EpisodeDetailsFragment() },
            containerId = R.id.fragmentContainerLayout,
            tag = EpisodeDetailsFragment::class.java.name
        )
}