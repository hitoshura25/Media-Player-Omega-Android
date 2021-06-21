package com.vmenon.mpo.common.framework.di.dagger

import com.vmenon.mpo.auth.framework.di.dagger.AuthComponent
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.common.framework.retrofit.MediaPlayerOmegaRetrofitService
import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [AuthComponent::class, PersistenceComponent::class],
    modules = [CommonFrameworkModule::class, ApiModule::class]
)
@CommonFrameworkScope
interface CommonFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkModule(module: CommonFrameworkModule): Builder
        fun authComponent(component: AuthComponent): Builder
        fun persistenceComponent(component: PersistenceComponent): Builder
        @BindsInstance fun systemFrameworkComponent(component: SystemFrameworkComponent): Builder
        fun build(): CommonFrameworkComponent
    }

    fun api(): MediaPlayerOmegaRetrofitService
    fun persistenceComponent(): PersistenceComponent
    fun authComponent(): AuthComponent
    fun systemFrameworkComponent(): SystemFrameworkComponent
    fun system(): System
}