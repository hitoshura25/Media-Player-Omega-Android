package com.vmenon.mpo.navigation.framework

import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin

data class ActivityOrigin<L : NavigationLocation<*>>(override val location: L) : NavigationOrigin<L>