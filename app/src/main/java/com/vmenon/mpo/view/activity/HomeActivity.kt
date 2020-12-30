package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.DrawerNavigationDestination
import com.vmenon.mpo.view.DrawerNavigationLocation
import com.vmenon.mpo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeActivity : BaseActivity<ActivityComponent>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(HomeLocation) {
    @Inject
    lateinit var libraryDestination: NavigationDestination<MyLibraryNavigationLocation>

    @Inject
    lateinit var showsDestination: NavigationDestination<SubscribedShowsLocation>

    @Inject
    lateinit var downloadsDestination: NavigationDestination<DownloadsLocation>

    private val viewModel: HomeViewModel by viewModel()

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.let { ab ->
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        nav_drawer_view.setNavigationItemSelectedListener { menuItem ->
            val location = when (menuItem.itemId) {
                else -> DrawerNavigationLocation(com.vmenon.mpo.view.R.id.nav_home)
            }
            navigationController.navigate(
                this,
                DrawerNavigationDestination(location)
            )
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override val location: HomeLocation
        get() = HomeLocation
}
