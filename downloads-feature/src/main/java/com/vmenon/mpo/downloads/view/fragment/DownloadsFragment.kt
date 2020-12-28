package com.vmenon.mpo.downloads.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.downloads.R
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.di.dagger.DownloadsComponentProvider
import com.vmenon.mpo.downloads.view.adapter.DownloadsAdapter
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_downloads.*

class DownloadsFragment : BaseFragment<DownloadsComponent>(), NavigationOrigin<NoNavigationParams> {
    private val viewModel: DownloadsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        downloadsList.layoutManager = LinearLayoutManager(context)
        downloadsList.setHasFixedSize(true)
        downloadsList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.downloads.observe(
            this,
            Observer { downloads ->
                when (downloads) {
                    LoadingState -> {
                    }
                    ErrorState -> {
                    }
                    is SuccessState -> {
                        val adapter = DownloadsAdapter(downloads.result)
                        downloadsList.adapter = adapter
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.downloads)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("${javaClass.name} onCreateView")

        return inflater.inflate(R.layout.fragment_downloads, container, false)
    }

    override fun setupComponent(context: Context): DownloadsComponent =
        (context as DownloadsComponentProvider).downloadsComponent()

    override fun inject(component: DownloadsComponent) {
        component.inject(this)
        component.inject(viewModel)
    }
}