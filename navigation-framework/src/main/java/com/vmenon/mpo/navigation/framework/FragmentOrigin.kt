package com.vmenon.mpo.navigation.framework

import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import java.lang.IllegalArgumentException

data class FragmentOrigin<L : NavigationLocation<*>>(
    override val location: L
) : NavigationOrigin<L> {
    companion object {
        inline fun <reified L : NavigationLocation<*>> create(): FragmentOrigin<L> =
            if (L::class.objectInstance != null) FragmentOrigin(L::class.objectInstance as L)
            else throw IllegalArgumentException(
                "${L::class.qualifiedName} is not an object instance!"
            )
    }
}