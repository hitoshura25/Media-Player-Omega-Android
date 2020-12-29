package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity<ActivityComponent, HomeLocation>() {
    @Inject
    lateinit var libraryDestination: NavigationDestination<MyLibraryNavigationLocation>

    @Inject
    lateinit var showsDestination: NavigationDestination<SubscribedShowsLocation>

    @Inject
    lateinit var downloadsDestination: NavigationDestination<DownloadsLocation>

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
                R.id.nav_library -> navigationController.navigate(this, libraryDestination)
                R.id.nav_downloads -> navigationController.navigate(this, downloadsDestination)
                R.id.nav_none -> {
                }
                else -> navigationController.navigate(this, showsDestination)
            }
            true
        }

        navigation.setOnNavigationItemReselectedListener {

        }

        if (savedInstanceState == null) {
            navigationController.navigate(this, showsDestination)
        }
        viewModel.currentLocation.observe(this, Observer { location ->
            println("Emitted location $location")
            val currentItemId = navigation.selectedItemId
            val newItemId = when (location) {
                is SubscribedShowsLocation -> R.id.nav_home
                is MyLibraryNavigationLocation -> R.id.nav_library
                is DownloadsLocation -> R.id.nav_downloads
                else -> R.id.nav_none
            }
            if (newItemId != currentItemId) {
                navigation.selectedItemId = newItemId
            }
        })
    }

    override val location: HomeLocation
        get() = HomeLocation
}
