package com.vmenon.mpo.downloads.view.activity

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState

import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import com.vmenon.mpo.downloads.view.adapter.DownloadsAdapter
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import kotlinx.android.synthetic.main.activity_downloads.*

class DownloadsActivity : BaseDrawerActivity<DownloadsComponent, NoNavigationParams>() {
    private val viewModel: DownloadsViewModel by viewModel()

    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadsList.layoutManager = LinearLayoutManager(this)
        downloadsList.setHasFixedSize(true)
        downloadsList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel.downloads.observe(
            this,
            Observer { downloads ->
                when (downloads) {
                    LoadingState -> {}
                    ErrorState -> {}
                    is SuccessState -> {
                        val adapter = DownloadsAdapter(downloads.result)
                        downloadsList.adapter = adapter
                    }
                }
            }
        )
    }

    override fun setupComponent(context: Context): DownloadsComponent =
        (context as DownloadsComponentProvider).downloadsComponent()

    override fun inject(component: DownloadsComponent) {
        component.inject(this)
        component.inject(viewModel)
    }
}
