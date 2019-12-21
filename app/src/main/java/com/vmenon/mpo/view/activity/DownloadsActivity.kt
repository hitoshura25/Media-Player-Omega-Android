package com.vmenon.mpo.view.activity

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.DownloadsAdapter
import com.vmenon.mpo.core.DownloadManager
import com.vmenon.mpo.di.AppComponent
import kotlinx.android.synthetic.main.activity_downloads.*

import javax.inject.Inject

class DownloadsActivity : BaseDrawerActivity() {

    @Inject
    lateinit var downloadManager: DownloadManager

    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadsList.layoutManager = LinearLayoutManager(this)
        downloadManager.downloads.observe(this, Observer { downloads ->
            val adapter = DownloadsAdapter(downloads)
            downloadsList.adapter = adapter
        })
    }
}
