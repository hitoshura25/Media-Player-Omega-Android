package com.vmenon.mpo.library.di.dagger

import androidx.fragment.app.Fragment
import com.vmenon.mpo.api.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.downloads.domain.DownloadsService
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.my_library.data.EpisodePersistenceDataSource
import com.vmenon.mpo.my_library.data.MyLibraryRepository
import com.vmenon.mpo.my_library.data.ShowPersistenceDataSource
import com.vmenon.mpo.my_library.data.ShowUpdateDataSource
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationDestination
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.my_library.domain.SubscribedShowsDestination
import com.vmenon.mpo.my_library.framework.MpoRetrofitApiShowUpdateDataSource
import com.vmenon.mpo.my_library.framework.RoomEpisodePersistenceDataSource
import com.vmenon.mpo.my_library.framework.RoomShowPersistenceDataSource
import com.vmenon.mpo.my_library.usecases.*
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.framework.FragmentDestination
import com.vmenon.mpo.persistence.room.dao.EpisodeDao
import com.vmenon.mpo.persistence.room.dao.ShowDao
import com.vmenon.mpo.player.domain.PlayerNavigationDestination
import com.vmenon.mpo.player.domain.PlayerRequestMapper
import com.vmenon.mpo.search.domain.SearchNavigationDestination
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
        playerNavigationDestination: PlayerNavigationDestination,
        searchNavigationDestination: SearchNavigationDestination
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
    fun provideLibraryNavigationDestination(): MyLibraryNavigationDestination =
        object : FragmentDestination, MyLibraryNavigationDestination {
            override val fragmentCreator: () -> Fragment
                get() = { LibraryFragment() }
            override val containerId: Int = R.id.fragmentContainerLayout
            override val tag: String
                get() = LibraryFragment::class.java.name
        }

    @Provides
    fun provideShowsNavigationDestination(): SubscribedShowsDestination =
        object : FragmentDestination, SubscribedShowsDestination {
            override val fragmentCreator: () -> Fragment
                get() = { SubscribedShowsFragment() }
            override val containerId: Int = R.id.fragmentContainerLayout
            override val tag: String
                get() = SubscribedShowsFragment::class.java.name
        }
}