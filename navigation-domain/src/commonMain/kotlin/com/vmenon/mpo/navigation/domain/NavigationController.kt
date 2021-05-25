package com.vmenon.mpo.navigation.domain

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface NavigationController {
    fun <P : NavigationParams, L : NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    )

    fun <L : NavigationLocation<NoNavigationParams>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>
    ) {
        navigate(navigationOrigin, navigationDestination, NoNavigationParams)
    }

    fun setOrigin(navigationOrigin: NavigationOrigin<*>)

    fun getNavigationParamJson(navigationOrigin: NavigationOrigin<*>): String?

    fun <P: NavigationParams> parseParams(navigationOrigin: NavigationOrigin<P>): P?

    val currentLocation: Flow<NavigationLocation<*>>
}

inline fun <reified P : NavigationParams> NavigationController.getOptionalParams(
    navigationOrigin: NavigationOrigin<P>
): P? {
    getNavigationParamJson(navigationOrigin)?.let { json ->
        return Json.decodeFromString(json)
    }

    return parseParams(navigationOrigin)
}

inline fun <reified P : NavigationParams> NavigationController.getParams(
    navigationOrigin: NavigationOrigin<P>
): P = getOptionalParams(navigationOrigin) ?: throw IllegalArgumentException()

object NavController {
    private val origin: MutableSharedFlow<NavigationLocation<*>> = MutableSharedFlow()
    private val mainScope = MainScope()

    val currentLocation: Flow<NavigationLocation<*>>
        get() = origin.asSharedFlow()

    fun setOrigin(navigationOrigin: NavigationOrigin<*>) {
        mainScope.launch {
            origin.emit(navigationOrigin.location)
        }
    }

    fun <P : NavigationParams, L : NavigationLocation<P>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>,
        navigationParams: P
    ) {
        navigationOrigin.navigateTo(navigationDestination, navigationParams)
    }

    fun <L : NavigationLocation<NoNavigationParams>> navigate(
        navigationOrigin: NavigationOrigin<*>,
        navigationDestination: NavigationDestination<L>
    ) {
        navigate(navigationOrigin, navigationDestination, NoNavigationParams)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified P : NavigationParams> getParams(
        navigationOrigin: NavigationOrigin<P>
    ): P {
        return getOptionalParams(navigationOrigin)
            ?: throw IllegalArgumentException("required parameters were not set!")
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified P : NavigationParams> getOptionalParams(navigationOrigin: NavigationOrigin<P>): P? {
        navigationOrigin.getNavigationParamJson()?.let { json ->
            return Json.decodeFromString(json)
        }

        return null
    }
}