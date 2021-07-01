package com.vmenon.mpo.my_library.framework.di.dagger

import com.vmenon.mpo.common.framework.di.dagger.CommonFrameworkComponent
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryService
import com.vmenon.mpo.player.domain.PlayerRequestMapper
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
    fun episodePlayerRequestMapper(): PlayerRequestMapper<EpisodeModel>
}