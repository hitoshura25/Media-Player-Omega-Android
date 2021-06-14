package com.vmenon.mpo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.vmenon.mpo.HomeLocation
import com.vmenon.mpo.HomeNavigationParams
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.databinding.ActivityMainBinding
import com.vmenon.mpo.di.ActivityComponent
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
    lateinit var playerDestination: NavigationDestination<PlayerNavigationLocation>

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

        binding.navigation.setupWithNavController(getNavController())
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

    private fun getNavController() =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    override fun getContentView(): View = binding.homeContentRoot

    override fun getLoadingView(): View = binding.loadingOverlayView.root
}
