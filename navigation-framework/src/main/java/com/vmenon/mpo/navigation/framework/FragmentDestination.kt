package com.vmenon.mpo.navigation.framework

import androidx.fragment.app.Fragment
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation

data class FragmentDestination<L : NavigationLocation<*>>(
    val fragmentCreator: () -> Fragment,
    val containerId: Int,
    val tag: String
) : NavigationDestination<L>