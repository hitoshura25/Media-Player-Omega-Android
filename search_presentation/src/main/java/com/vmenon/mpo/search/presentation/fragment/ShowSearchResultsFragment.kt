package com.vmenon.mpo.search.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.presentation.R
import com.vmenon.mpo.search.presentation.databinding.FragmentShowSearchResultsBinding
import com.vmenon.mpo.search.presentation.di.dagger.SearchComponent
import com.vmenon.mpo.search.presentation.di.dagger.toSearchComponent
import com.vmenon.mpo.navigation.domain.search.SearchNavigationLocation
import com.vmenon.mpo.navigation.domain.search.SearchNavigationParams
import com.vmenon.mpo.navigation.domain.search.ShowDetailsLocation
import com.vmenon.mpo.navigation.domain.search.ShowDetailsParams
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.presentation.mvi.ShowSearchViewEvent
import com.vmenon.mpo.search.presentation.adapter.ShowSearchResultsAdapter
import com.vmenon.mpo.search.presentation.viewmodel.ShowSearchResultsViewModel
import com.vmenon.mpo.view.BaseViewBindingFragment
import com.vmenon.mpo.view.LoadingStateHelper
import javax.inject.Inject

class ShowSearchResultsFragment :
    BaseViewBindingFragment<SearchComponent, FragmentShowSearchResultsBinding>(),
    ShowSearchResultsAdapter.ShowSelectedListener,
    NavigationOrigin<SearchNavigationParams> by NavigationOrigin.from(SearchNavigationLocation) {

    @Inject
    lateinit var showDetailsDestination: NavigationDestination<ShowDetailsLocation>

    private val showSearchResultsViewModel: ShowSearchResultsViewModel by viewModel()

    private lateinit var adapter: ShowSearchResultsAdapter
    private var loadingStateHelper: LoadingStateHelper? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingStateHelper = LoadingStateHelper.switchWithContent(
            loadingView = binding.contentProgressBar,
            contentView = binding.searchResultsContainer
        )
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.showList.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(requireContext())
        binding.showList.layoutManager = layoutManager
        binding.showList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = ShowSearchResultsAdapter()
        adapter.setListener(this)
        binding.showList.adapter = adapter

        navigationController.setupWith(
            this,
            binding.toolbar,
            drawerLayout(),
            navigationView(),
        )

        val query = navigationController.getParams(this).query
        binding.toolbar.title = this.getString(R.string.show_search_title, query)

        showSearchResultsViewModel.send(ShowSearchViewEvent.SearchRequestedEvent(query))
        showSearchResultsViewModel.states().observe(viewLifecycleOwner, { state ->
            state.unhandledContent()?.let { stateContent ->
                if (stateContent.loading) {
                    loadingStateHelper?.showLoadingState()
                } else {
                    loadingStateHelper?.showContentState()
                }

                if (stateContent.currentResults.isEmpty()) {
                    binding.noShowsText.visibility = View.VISIBLE
                    binding.showList.visibility = View.GONE
                } else {
                    binding.showList.visibility = View.VISIBLE
                    binding.noShowsText.visibility = View.GONE
                }

                if (stateContent.diffResult != null) {
                    adapter.update(stateContent.currentResults, stateContent.diffResult)
                }
            }
        })
    }

    override fun onShowSelected(show: ShowSearchResultModel) {
        navigationController.navigate(this, showDetailsDestination, ShowDetailsParams(show.id))
    }

    override fun setupComponent(context: Context): SearchComponent = context.toSearchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showSearchResultsViewModel)
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentShowSearchResultsBinding.inflate(inflater, container, false)

    override fun onDestroyView() {
        super.onDestroyView()
        showSearchResultsViewModel.clearState()
        loadingStateHelper = null
    }
}