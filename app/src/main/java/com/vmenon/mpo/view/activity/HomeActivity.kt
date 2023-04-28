package com.vmenon.mpo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.splitcompat.SplitCompat
import com.vmenon.mpo.BuildConfig
import com.vmenon.mpo.R
import com.vmenon.mpo.common.framework.livedata.observeUnhandled
import com.vmenon.mpo.databinding.ActivityMainBinding
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.di.AppComponentProvider
import com.vmenon.mpo.model.BiometricsState.*
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.navigation.domain.root.RootLocation
import com.vmenon.mpo.viewmodel.HomeViewModel

class HomeActivity : BaseViewBindingActivity<ActivityComponent, ActivityMainBinding>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(RootLocation) {

    private val viewModel: HomeViewModel by viewModel()

    override fun setupComponent(context: Context): ActivityComponent =
        (context as AppComponentProvider).appComponent().activityComponent().create()

    override fun inject(component: ActivityComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment: NavHostFragment = if (BuildConfig.DYNAMIC_FEATURES) {
            DynamicNavHostFragment.create(R.navigation.nav_graph)
        } else {
            NavHostFragment.create(R.navigation.nav_graph)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .runOnCommit(::setupNavigationViews)
            .commit()

        viewModel.registerForBiometrics(this).observeUnhandled(this) { state ->
            when (state) {
                is PromptToEnroll -> viewModel.promptForBiometricEnrollment(state.request)
                is PromptToStayAuthenticated -> {
                    logger.println("HomeActivity::Received PromptToStayAuthenticated")
                    if (state.enrollmentRequired) {
                        logger.println("HomeActivity::Asked to askToEnrollInBiometrics")
                        askToEnrollInBiometrics()
                    } else {
                        logger.println("HomeActivity::Prompted ForBiometricsToStayAuthenticated")
                        viewModel.promptForBiometricsToStayAuthenticated(this)
                    }
                }
                is PromptAfterEnrollment ->
                    viewModel.promptForBiometricsAfterEnrollment(this, state.request)
            }
        }
    }

    override fun bind(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
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

    override fun getContentView(): View = binding.homeContentRoot
    override fun getLoadingView(): View = binding.loadingOverlayView.root
    override fun drawerLayout(): DrawerLayout = binding.drawerLayout
    override fun navigationView(): NavigationView = binding.navDrawerView

    private fun setupNavigationViews() {
        navigationController.setupWith(this, binding.navigation)
    }

    private fun askToEnrollInBiometrics() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.promptForBiometricsToStayAuthenticated(this@HomeActivity)
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->

            }
            setTitle(getString(R.string.enroll_in_biometrics))
            setMessage(getString(R.string.re_enroll_in_biometrics))
        }
        builder.create().show()
    }
}
