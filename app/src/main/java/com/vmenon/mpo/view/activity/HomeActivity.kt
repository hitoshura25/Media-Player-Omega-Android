package com.vmenon.mpo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.splitcompat.SplitCompat
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.common.framework.livedata.observeUnhandled
import com.vmenon.mpo.databinding.ActivityMainBinding
import com.vmenon.mpo.di.ActivityComponent
import com.vmenon.mpo.model.BiometricsState.*
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.navigation.domain.root.RootLocation
import com.vmenon.mpo.viewmodel.HomeViewModel

class HomeActivity : BaseActivity<ActivityComponent>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(RootLocation) {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun setupComponent(context: Context): ActivityComponent =
        (context as MPOApplication).appComponent.activityComponent().create()

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigationController.setupWith(this, binding.navigation)
        viewModel.registerForBiometrics(this).observeUnhandled(this) { state ->
            when (state) {
                is PromptToEnroll -> viewModel.promptForBiometricEnrollment(state.request)
                is PromptToStayAuthenticated -> {
                    if (state.enrollmentRequired) {
                        askToEnrollInBiometrics()
                    } else {
                        viewModel.promptForBiometricsToStayAuthenticated(this)
                    }
                }
                is PromptAfterEnrollment ->
                    viewModel.promptForBiometricsAfterEnrollment(this, state.request)
            }
        }
    }

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
