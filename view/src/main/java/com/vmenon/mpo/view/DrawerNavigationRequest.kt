package com.vmenon.mpo.view

import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationParams
import com.vmenon.mpo.navigation.domain.NavigationRequest

class DrawerNavigationDestination(val menuId: Int) : NavigationDestination

class DrawerNavigationParams : NavigationParams

class DrawerNavigationRequest(
    override val destination: DrawerNavigationDestination,
    override val params: DrawerNavigationParams
) : NavigationRequest<DrawerNavigationDestination, DrawerNavigationParams>