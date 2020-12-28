package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.domain.DownloadsDestination
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationDestination
import com.vmenon.mpo.my_library.domain.SubscribedShowsDestination
import com.vmenon.mpo.navigation.domain.NavigationRequest
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity<ActivityComponent, NoNavigationParams>() {
    @Inject
    lateinit var libraryDestination: MyLibraryNavigationDestination

    @Inject
    lateinit var showsDestination: SubscribedShowsDestination

    @Inject
    lateinit var downloadsDestination: DownloadsDestination

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> navigationController.onNavigationSelected(
                    object :
                        NavigationRequest<MyLibraryNavigationDestination, NoNavigationParams> {
                        override val destination: MyLibraryNavigationDestination
                            get() = libraryDestination
                        override val params: NoNavigationParams
                            get() = NoNavigationParams

                    },
                    this
                )
                R.id.nav_downloads -> navigationController.onNavigationSelected(
                    object :
                        NavigationRequest<DownloadsDestination, NoNavigationParams> {
                        override val destination: DownloadsDestination
                            get() = downloadsDestination
                        override val params: NoNavigationParams
                            get() = NoNavigationParams

                    },
                    this
                )
                else -> navigationController.onNavigationSelected(
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

        if (savedInstanceState == null) {
            navigationController.onNavigationSelected(
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
    }
}
