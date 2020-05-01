package com.vmenon.mpo.downloads.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import com.vmenon.mpo.downloads.view.adapter.DownloadsAdapter
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import kotlinx.android.synthetic.main.activity_downloads.*

class DownloadsActivity : BaseDrawerActivity() {
    private val viewModel by lazy {
        viewModel() as DownloadsViewModel
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as DownloadsComponentProvider).downloadsComponent().apply {
            inject(this@DownloadsActivity)
            inject(viewModel)
        }

        downloadsList.layoutManager = LinearLayoutManager(this)
        downloadsList.setHasFixedSize(true)
        downloadsList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            viewModel.downloads
                .subscribe(
                    { downloads ->
                        val adapter = DownloadsAdapter(downloads)
                        downloadsList.adapter = adapter
                    },
                    { throwable ->
                        throwable.printStackTrace()
                    }
                )
        )
    }
}
