package com.vmenon.mpo.navigation.framework

import androidx.fragment.app.Fragment
import com.vmenon.mpo.navigation.domain.NavigationDestination

interface FragmentDestination: NavigationDestination {
    val fragmentCreator: () -> Fragment
    val containerId: Int
    val tag: String
}