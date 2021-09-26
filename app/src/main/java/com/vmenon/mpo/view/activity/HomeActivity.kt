package com.vmenon.mpo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.splitcompat.SplitCompat
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.R
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.domain.biometrics.PromptRequest
import com.vmenon.mpo.databinding.ActivityMainBinding
import com.vmenon.mpo.di.ActivityComponent
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
        viewModel.biometricPromptRequested.observe(this) { reason ->
            val request = when (reason) {
                PromptReason.ENROLLMENT -> PromptRequest(
                    title = getString(R.string.enroll_in_biometrics),
                    subtitle = getString(R.string.confirm_to_complete_enrollment),
                    confirmationRequired = false,
                    negativeActionText = getString(R.string.cancel)
                )
                PromptReason.AUTHENTICATE -> PromptRequest(
                    title = getString(R.string.authenticate),
                    subtitle = getString(R.string.confirm_to_stay_authenticated),
                    confirmationRequired = false,
                    negativeActionText = getString(R.string.logout)
                )
                else -> null
            }
            if (request != null) {
                viewModel.showBiometricPrompt(this, request)
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
}
