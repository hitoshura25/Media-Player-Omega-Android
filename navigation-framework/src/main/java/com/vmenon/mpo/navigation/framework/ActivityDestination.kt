package com.vmenon.mpo.navigation.framework

import com.vmenon.mpo.navigation.domain.NavigationDestination

interface ActivityDestination : NavigationDestination {
    val activityClass: Class<*>
}