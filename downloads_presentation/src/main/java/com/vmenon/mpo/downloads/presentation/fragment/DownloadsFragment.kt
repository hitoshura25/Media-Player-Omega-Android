package com.vmenon.mpo.downloads.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.downloads.presentation.databinding.FragmentDownloadsBinding
import com.vmenon.mpo.downloads.presentation.di.dagger.DownloadsComponent
import com.vmenon.mpo.downloads.presentation.di.dagger.toDownloadsComponent
import com.vmenon.mpo.navigation.domain.downloads.DownloadsLocation
import com.vmenon.mpo.downloads.presentation.adapter.DownloadsAdapter
import com.vmenon.mpo.downloads.presentation.viewmodel.DownloadsViewModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment

class DownloadsFragment : BaseViewBindingFragment<DownloadsComponent, FragmentDownloadsBinding>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(DownloadsLocation) {
    private val viewModel: DownloadsViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationController.setupWith(
            this,
            binding.toolbar,
            drawerLayout(),
            navigationView()
        )
        binding.downloadsList.layoutManager = LinearLayoutManager(context)
        binding.downloadsList.setHasFixedSize(true)
        binding.downloadsList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        viewModel.downloads.observe(
            viewLifecycleOwner,
            { downloads ->
                when (downloads) {
                    LoadingState -> {
                    }
                    ErrorState -> {
                    }
                    is SuccessState -> {
                        val adapter = DownloadsAdapter(downloads.result)
                        binding.downloadsList.adapter = adapter
                    }
                    else -> {
                    }
                }
            }
        )
    }

    override fun setupComponent(context: Context): DownloadsComponent =
        context.toDownloadsComponent()

    override fun inject(component: DownloadsComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentDownloadsBinding =
        FragmentDownloadsBinding.inflate(inflater, container, false)
}