package com.vmenon.mpo.library.view.fragment

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
import com.vmenon.mpo.library.databinding.FragmentLibraryBinding
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.toLibraryComponent
import com.vmenon.mpo.library.view.adapter.LibraryAdapter
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import com.vmenon.mpo.my_library.domain.EpisodeDetailsLocation
import com.vmenon.mpo.my_library.domain.EpisodeDetailsParams
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.navigation.domain.my_library.MyLibraryNavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment
import javax.inject.Inject

class LibraryFragment : BaseViewBindingFragment<LibraryComponent, FragmentLibraryBinding>(),
    LibraryAdapter.LibrarySelectedListener,
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(MyLibraryNavigationLocation) {

    @Inject
    lateinit var episodeDetailsDestination: NavigationDestination<EpisodeDetailsLocation>

    private val viewModel: LibraryViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationController.setupWith(
            this,
            binding.toolbar,
            drawerLayout(),
            navigationView()
        )
        val layoutManager = LinearLayoutManager(context)
        binding.libraryList.setHasFixedSize(true)
        binding.libraryList.layoutManager = layoutManager
        binding.libraryList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.allEpisodes().observe(
            viewLifecycleOwner,
            { episodes ->
                when (episodes) {
                    is SuccessState -> {
                        val adapter = LibraryAdapter(episodes.result)
                        adapter.setListener(this@LibraryFragment)
                        binding.libraryList.adapter = adapter
                    }
                    LoadingState -> {
                    }
                    ErrorState -> {
                    }
                    else -> {
                    }
                }
            }
        )
    }

    override fun setupComponent(context: Context) = context.toLibraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onEpisodeSelected(episodeWithShowDetails: EpisodeModel) {
        navigationController.navigate(
            this,
            episodeDetailsDestination,
            EpisodeDetailsParams(episodeWithShowDetails.id)
        )
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentLibraryBinding =
        FragmentLibraryBinding.inflate(inflater, container, false)
}