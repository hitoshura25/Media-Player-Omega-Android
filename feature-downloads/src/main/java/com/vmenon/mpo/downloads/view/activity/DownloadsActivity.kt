package com.vmenon.mpo.downloads.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import com.vmenon.mpo.downloads.view.adapter.DownloadsAdapter
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import kotlinx.android.synthetic.main.activity_downloads.*
import javax.inject.Inject

class DownloadsActivity : BaseDrawerActivity() {

    lateinit var viewModel: DownloadsViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as DownloadsComponentProvider).downloadsComponent().inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[DownloadsViewModel::class.java]

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
