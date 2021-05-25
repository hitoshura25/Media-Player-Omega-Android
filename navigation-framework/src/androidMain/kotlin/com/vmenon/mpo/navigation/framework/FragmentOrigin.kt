package com.vmenon.mpo.navigation.framework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NavigationParams
import kotlinx.serialization.encodeToString

class FragmentOrigin<P : NavigationParams>(
    override val location: NavigationLocation<P>
) : NavigationOrigin<P>, Fragment() {

    companion object {
        fun <P : NavigationParams> from(location: NavigationLocation<P>) = FragmentOrigin(location)
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

    override fun getNavigationParamJson(): String? = arguments?.getString(
        ActivityDestination.EXTRA_NAVIGATION_BUNDLE
    )

    private fun handleActivityDestination(
        navigationDestination: ActivityDestination<*>,
        params: NavigationParams
    ) {
        val context = requireActivity()
        val intent = navigationDestination.createIntent(context, params)
        startActivityForNavigation(intent, context)
    }

    private fun handleFragmentDestination(
        navigationDestination: FragmentDestination<*>,
        params: NavigationParams
    ) {
        val fragmentManager = parentFragmentManager
        val destinationFragment =
            parentFragmentManager.findFragmentByTag(navigationDestination.tag)
                ?: navigationDestination.fragmentCreator()
        destinationFragment.arguments =
            Bundle().apply {
                putString(
                    ActivityDestination.EXTRA_NAVIGATION_BUNDLE,
                    NavigationParamsSerializer.format.encodeToString(params)
                )
            }
        fragmentManager.beginTransaction()
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