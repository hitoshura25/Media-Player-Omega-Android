package com.vmenon.mpo.navigation.framework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NavigationParams
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ActivityOrigin<P : NavigationParams>(override val location: NavigationLocation<P>) :
    NavigationOrigin<P>, FragmentActivity() {
    companion object {
        fun <P : NavigationParams> from(location: NavigationLocation<P>) = ActivityOrigin(location)
    }

    override fun <P : NavigationParams, L : NavigationLocation<P>> navigateTo(
        destination: NavigationDestination<L>,
        params: P
    ) {
        when (destination) {
            is ActivityDestination -> handleActivityDestination(destination, params)
            is FragmentDestination -> handleFragmentDestination(destination, params)
            else -> {
                throw IllegalArgumentException("request.destination is invalid or unsupported!")
            }
        }
    }

    override fun getNavigationParamJson(): String? =
        intent.getStringExtra(ActivityDestination.EXTRA_NAVIGATION_BUNDLE)

    private fun handleActivityDestination(
        navigationDestination: ActivityDestination<*>,
        params: NavigationParams
    ) {
        val intent = navigationDestination.createIntent(this, params)
        startActivityForNavigation(intent, this)
    }

    private fun handleFragmentDestination(
        navigationDestination: FragmentDestination<*>,
        params: NavigationParams
    ) {
        val destinationFragment =
            supportFragmentManager.findFragmentByTag(navigationDestination.tag)
                ?: navigationDestination.fragmentCreator()
        destinationFragment.arguments =
            Bundle().apply {
                putString(
                    ActivityDestination.EXTRA_NAVIGATION_BUNDLE,
                    NavigationParamsSerializer.format.encodeToString(params)
                )
            }
        supportFragmentManager.beginTransaction()
            .replace(
                navigationDestination.containerId,
                destinationFragment,
                navigationDestination.tag
            )
            .addToBackStack(null)
            .commit()
    }

    private fun startActivityForNavigation(intent: Intent, context: Context) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}