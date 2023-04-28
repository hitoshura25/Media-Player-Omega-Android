package com.vmenon.mpo.navigation.framework

import android.content.Context
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import org.junit.Test
import org.mockito.kotlin.mock

class DefaultNavigationControllerTest {

    private val invalidOrigin = object : NavigationOrigin<NoNavigationParams> {
        override val location: NavigationLocation<NoNavigationParams>
            get() = object : NavigationLocation<NoNavigationParams> {}
    }

    private val invalidDestination =
        object : NavigationDestination<NavigationLocation<NoNavigationParams>> {
            override val location: NavigationLocation<NoNavigationParams>
                get() = object : NavigationLocation<NoNavigationParams> {}
        }

    private val defaultNavigationController = DefaultNavigationController(
        emptyMap(),
        0,
        0,
    )

    @Test(expected = IllegalArgumentException::class)
    fun givenInvalidOriginWhenNavigateCalledThenShouldThrowIllegalArgument() {
        defaultNavigationController.navigate(invalidOrigin, invalidDestination)
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenInvalidOriginWhenGetParamsCalledThenShouldThrowIllegalArgument() {
        defaultNavigationController.getParams(invalidOrigin)
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenInvalidOriginWhenGetOptionalParamsCalledThenShouldThrowIllegalArgument() {
        defaultNavigationController.getOptionalParams(invalidOrigin)
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenInvalidContextWhenCreateNavigationRequestCalledThenShouldThrowIllegalArgument() {
        defaultNavigationController.createNavigationRequest<Any, NoNavigationParams>(
            -1L,
            NoNavigationParams,
            invalidDestination,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun givenValidContextAndInvalidDestinationWhenCreateNavigationRequestCalledThenShouldThrowIllegalArgument() {
        defaultNavigationController.createNavigationRequest<Any, NoNavigationParams>(
            mock<Context>(),
            NoNavigationParams,
            invalidDestination,
        )
    }
}