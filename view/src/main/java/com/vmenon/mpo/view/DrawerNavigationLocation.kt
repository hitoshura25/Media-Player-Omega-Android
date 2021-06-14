package com.vmenon.mpo.view

import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NoNavigationParams

data class DrawerNavigationLocation(val menuId: Int) : NavigationLocation<NoNavigationParams>
data class DrawerNavigationDestination(override val location: DrawerNavigationLocation) :
    NavigationDestination<DrawerNavigationLocation>
