package com.vmenon.mpo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.HomeNavigationParams
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.databinding.ActivityMainBinding
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.downloads.domain.DownloadsLocation
import com.vmenon.mpo.login.domain.LoginNavigationLocation
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.player.domain.PlayerNavigationLocation
import com.vmenon.mpo.player.domain.PlayerNavigationParams
import com.vmenon.mpo.view.DrawerNavigationDestination
import com.vmenon.mpo.view.DrawerNavigationLocation
import com.vmenon.mpo.viewmodel.HomeViewModel
import javax.inject.Inject

class HomeActivity : BaseActivity<ActivityComponent>(),
    NavigationOrigin<HomeNavigationParams> by NavigationOrigin.from(HomeLocation) {
    @Inject
    lateinit var libraryDestination: NavigationDestination<MyLibraryNavigationLocation>

    @Inject
    lateinit var showsDestination: NavigationDestination<SubscribedShowsLocation>

    @Inject
    lateinit var downloadsDestination: NavigationDestination<DownloadsLocation>

    @Inject
    lateinit var playerDestination: NavigationDestination<PlayerNavigationLocation>

    @Inject
    lateinit var loginDestination: NavigationDestination<LoginNavigationLocation>

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let { ab ->
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        binding.navDrawerView.setNavigationItemSelectedListener { menuItem ->
            val location = when (menuItem.itemId) {
                else -> DrawerNavigationLocation(com.vmenon.mpo.view.R.id.nav_home)
            }
            navigationController.navigate(
                this,
                DrawerNavigationDestination(location)
            )
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> navigationController.navigate(this, libraryDestination)
                R.id.nav_downloads -> navigationController.navigate(this, downloadsDestination)
                R.id.nav_account -> navigationController.navigate(this, loginDestination)
                R.id.nav_none -> {
                }
                else -> navigationController.navigate(this, showsDestination)
            }
            true
        }

        binding.navigation.setOnNavigationItemReselectedListener {

        }

        if (savedInstanceState == null) {
            navigationController.navigate(this, showsDestination)
        }

        viewModel.currentLocation.observe(this, { location ->
            println("Emitted location $location")
            val currentItemId = binding.navigation.selectedItemId
            val newItemId = when (location) {
                is SubscribedShowsLocation -> R.id.nav_home
                is MyLibraryNavigationLocation -> R.id.nav_library
                is DownloadsLocation -> R.id.nav_downloads
                is LoginNavigationLocation -> R.id.nav_account
                else -> R.id.nav_none
            }
            if (newItemId != currentItemId) {
                binding.navigation.selectedItemId = newItemId
            }
        })
        handleHomeNavigationParams()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleHomeNavigationParams()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleHomeNavigationParams() {
        navigationController.getOptionalParams(this)?.let { params ->
            params.playbackMediaRequest?.let { playbackMediaRequest ->
                navigationController.navigate(
                    this,
                    playerDestination,
                    PlayerNavigationParams(playbackMediaRequest)
                )
            }
        }
    }
}
