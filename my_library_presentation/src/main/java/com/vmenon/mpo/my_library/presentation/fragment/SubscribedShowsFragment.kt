package com.vmenon.mpo.my_library.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.vmenon.mpo.my_library.presentation.R
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.my_library.presentation.databinding.SubscribedShowsFragmentBinding
import com.vmenon.mpo.my_library.presentation.di.dagger.toLibraryComponent
import com.vmenon.mpo.my_library.presentation.adapter.SubscriptionGalleryAdapter
import com.vmenon.mpo.my_library.presentation.di.dagger.LibraryComponent
import com.vmenon.mpo.my_library.presentation.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.navigation.domain.my_library.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseViewBindingFragment

class SubscribedShowsFragment :
    BaseViewBindingFragment<LibraryComponent, SubscribedShowsFragmentBinding>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(SubscribedShowsLocation) {
    private val viewModel: SubscribedShowsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationController.setupWith(
            this,
            binding.toolbar,
            drawerLayout(),
            navigationView()
        )
        binding.toolbar.inflateMenu(R.menu.subscribed_shows_options_menu)
        // Associate searchable configuration with the SearchView
        val menuItem = binding.toolbar.menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.searchForShows(query, this@SubscribedShowsFragment)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            }
        )

        binding.showList.setHasFixedSize(true)
        binding.showList.layoutManager = GridLayoutManager(context, 3)
        viewModel.subscribedShows().observe(viewLifecycleOwner, { result ->
            when (result) {
                LoadingState -> {
                }
                is SuccessState -> {
                    binding.showList.adapter = SubscriptionGalleryAdapter(result.result)
                }
                ErrorState -> {
                }
                else -> {
                }
            }
        })
    }

    override fun setupComponent(context: Context) = context.toLibraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?) =
        SubscribedShowsFragmentBinding.inflate(inflater, container, false)
}