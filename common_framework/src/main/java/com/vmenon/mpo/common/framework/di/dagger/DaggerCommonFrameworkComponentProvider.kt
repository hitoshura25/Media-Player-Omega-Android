package com.vmenon.mpo.common.framework.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.navigation.framework.di.dagger.NavigationFrameworkComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent

class DaggerCommonFrameworkComponentProvider(
    systemFrameworkComponent: SystemFrameworkComponent,
    authComponent: AuthComponent,
    persistenceComponent: PersistenceComponent,
    navigationFrameworkComponent: NavigationFrameworkComponent,
    apiUrl: String
) : CommonFrameworkComponentProvider {
    private val commonFrameworkComponent = DaggerCommonFrameworkComponent.builder()
        .systemFrameworkComponent(systemFrameworkComponent)
        .authComponent(authComponent)
        .persistenceComponent(persistenceComponent)
        .navigationFrameworkComponent(navigationFrameworkComponent)
        .apiUrl(apiUrl)
        .build()

    override fun commonFrameworkComponent(): CommonFrameworkComponent = commonFrameworkComponent
}