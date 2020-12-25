package com.vmenon.mpo.downloads.view.activity

import android.content.Context

import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import com.vmenon.mpo.navigation.domain.NoNavigationParams

class DownloadsActivity : BaseDrawerActivity<DownloadsComponent, NoNavigationParams>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun setupComponent(context: Context): DownloadsComponent =
        (context as DownloadsComponentProvider).downloadsComponent()

    override fun inject(component: DownloadsComponent) {
        component.inject(this)
    }
}
