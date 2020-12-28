package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.domain.DownloadsDestination
import com.vmenon.mpo.downloads.view.fragment.DownloadsFragment
import com.vmenon.mpo.library.view.fragment.LibraryFragment
import com.vmenon.mpo.library.view.fragment.SubscribedShowsFragment
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationDestination
import com.vmenon.mpo.my_library.domain.SubscribedShowsDestination
import com.vmenon.mpo.navigation.domain.NavigationRequest
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity<ActivityComponent, NoNavigationParams>() {
    @Inject
    lateinit var libraryDestination: MyLibraryNavigationDestination

    @Inject
    lateinit var showsDestination: SubscribedShowsDestination

    @Inject
    lateinit var downloadsDestination: DownloadsDestination

    private val viewModel: HomeViewModel by viewModel()

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val navMenuId: Int
        get() = R.id.nav_home

    override val isRootActivity: Boolean
        get() = true

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> navigationController.navigate(
                    object :
                        NavigationRequest<MyLibraryNavigationDestination, NoNavigationParams> {
                        override val destination: MyLibraryNavigationDestination
                            get() = libraryDestination
                        override val params: NoNavigationParams
                            get() = NoNavigationParams

                    },
                    this
                )
                R.id.nav_downloads -> navigationController.navigate(
                    object :
                        NavigationRequest<DownloadsDestination, NoNavigationParams> {
                        override val destination: DownloadsDestination
                            get() = downloadsDestination
                        override val params: NoNavigationParams
                            get() = NoNavigationParams
                    },
                    this
                )
                R.id.nav_none -> {}
                else -> navigationController.navigate(
                    object :
                        NavigationRequest<SubscribedShowsDestination, NoNavigationParams> {
                        override val destination: SubscribedShowsDestination
                            get() = showsDestination
                        override val params: NoNavigationParams
                            get() = NoNavigationParams

                    },
                    this
                )
            }
            true
        }

        navigation.setOnNavigationItemReselectedListener {

        }

        if (savedInstanceState == null) {
            navigationController.navigate(
                object :
                    NavigationRequest<SubscribedShowsDestination, NoNavigationParams> {
                    override val destination: SubscribedShowsDestination
                        get() = showsDestination
                    override val params: NoNavigationParams
                        get() = NoNavigationParams

                },
                this
            )
        }
        viewModel.currentLocation.observe(this, Observer { location ->
            println("Emitted location $location")
            val currentItemId = navigation.selectedItemId
            // TODO: Make this cleaner (i.e. consolidate NavigationOrigin/Destination into a single interface)
            val newItemId = when (location) {
                is SubscribedShowsFragment -> R.id.nav_home
                is LibraryFragment -> R.id.nav_library
                is DownloadsFragment -> R.id.nav_downloads
                else -> R.id.nav_none
            }
            if (newItemId != currentItemId) {
                navigation.selectedItemId = newItemId
            }
        })
    }
}
