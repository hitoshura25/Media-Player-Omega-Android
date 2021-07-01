package com.vmenon.mpo.my_library.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.my_library.domain.MyLibraryService
import dagger.Component

@Component(
    dependencies = [CommonFrameworkComponent::class],
    modules = [LibraryFrameworkModule::class]
)
@LibraryFrameworkScope
interface LibraryFrameworkComponent {
    @Component.Builder
    interface Builder {
        fun commonFrameworkComponent(component: CommonFrameworkComponent): Builder
        fun build(): LibraryFrameworkComponent
    }

    fun myLibraryService(): MyLibraryService
}