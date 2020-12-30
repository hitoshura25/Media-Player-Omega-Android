package com.vmenon.mpo.navigation.framework

import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import java.lang.IllegalArgumentException

data class ActivityOrigin<L : NavigationLocation<*>>(override val location: L) :
    NavigationOrigin<L> {
    companion object {
        inline fun <reified L : NavigationLocation<*>> create(): ActivityOrigin<L> =
            if (L::class.objectInstance != null) ActivityOrigin(L::class.objectInstance as L)
            else throw IllegalArgumentException(
                "${L::class.qualifiedName} is not an object instance!"
            )
    }
}