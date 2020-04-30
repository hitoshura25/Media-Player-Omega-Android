package com.vmenon.mpo.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.MPOApplication

import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.DownloadsAdapter
import com.vmenon.mpo.viewmodel.DownloadsViewModel
import kotlinx.android.synthetic.main.activity_downloads.*
import javax.inject.Inject

class DownloadsActivity : BaseDrawerActivity() {

    @Inject
    lateinit var viewModel: DownloadsViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_downloads

    override val navMenuId: Int
        get() = R.id.nav_downloads

    override val isRootActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MPOApplication).appComponent.activityComponent().create().inject(this)

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
