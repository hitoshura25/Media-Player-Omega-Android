package com.vmenon.mpo.persistence.room.test.di.dagger

import com.vmenon.mpo.persistence.di.dagger.PersistenceComponent
import com.vmenon.mpo.persistence.di.dagger.PersistenceModule
import com.vmenon.mpo.persistence.di.dagger.PersistenceScope
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponent
import dagger.Component

@Component(
    dependencies = [SystemFrameworkComponent::class],
    modules = [PersistenceModule::class, TestDatabaseModule::class]
)
@PersistenceScope
interface TestPersistenceComponent : PersistenceComponent