package com.vmenon.mpo.library.view.activity

import android.content.Context

import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider

import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.activity.BaseDrawerActivity

class LibraryActivity : BaseDrawerActivity<LibraryComponent, NoNavigationParams>(){

    override val layoutResourceId: Int
        get() = R.layout.activity_library

    override val navMenuId: Int
        get() = R.id.nav_library

    override val isRootActivity: Boolean
        get() = true


    override fun setupComponent(context: Context): LibraryComponent =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
    }
}