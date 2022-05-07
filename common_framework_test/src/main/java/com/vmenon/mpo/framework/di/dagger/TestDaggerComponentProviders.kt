package com.vmenon.mpo.framework.di.dagger

import android.app.Application
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.test.di.dagger.DaggerTestAuthComponent
import com.vmenon.mpo.auth.test.di.dagger.DaggerTestBiometricsComponent
import com.vmenon.mpo.common.framework.di.dagger.*
import com.vmenon.mpo.navigation.domain.*
import com.vmenon.mpo.navigation.domain.player.PlayerNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkComponent
import com.vmenon.mpo.persistence.room.test.di.dagger.DaggerTestPersistenceComponent
import com.vmenon.mpo.system.domain.BuildConfigProvider
import com.vmenon.mpo.system.framework.di.dagger.DaggerSystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider
import org.mockito.kotlin.mock

class TestDaggerComponentProviders(
    application: Application
) : SystemFrameworkComponentProvider, AuthComponentProvider,
    CommonFrameworkComponentProvider {

    private val buildConfigProvider: BuildConfigProvider = mock()

    private val systemFrameworkComponent = DaggerSystemFrameworkComponent.builder()
        .application(application)
        .buildConfigProvider(buildConfigProvider)
        .build()

    private val authComponent = DaggerTestAuthComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .testBiometricsComponent(
            DaggerTestBiometricsComponent.builder()
                .systemFrameworkComponent(systemFrameworkComponent)
                .build()
        )
        .build()

    val navigationController: NavigationController = mock()
    val playerNavigationDestination: NavigationDestination<PlayerNavigationLocation> = mock()
    val searchNavigationDestination: NavigationDestination<SearchNavigationLocation> = mock()

    private val navigationFrameworkComponent = object : NavigationFrameworkComponent {
        override fun searchNavigationDestination(): NavigationDestination<SearchNavigationLocation> =
            searchNavigationDestination

        override fun playerNavigationDestination(): NavigationDestination<PlayerNavigationLocation> =
            playerNavigationDestination

        override fun navigationController(): NavigationController = navigationController
    }

    private val commonFrameworkComponentProvider = DaggerCommonFrameworkComponentProvider(
        systemFrameworkComponent,
        authComponent,
        DaggerTestPersistenceComponent.builder()
            .systemFrameworkComponent(systemFrameworkComponent)
            .build(),
        navigationFrameworkComponent,
        "https://localhost:8080/"
    )

    override fun authComponent(): AuthComponent = authComponent

    override fun commonFrameworkComponent(): CommonFrameworkComponent =
        commonFrameworkComponentProvider.commonFrameworkComponent()

    override fun systemFrameworkComponent(): SystemFrameworkComponent = systemFrameworkComponent
}