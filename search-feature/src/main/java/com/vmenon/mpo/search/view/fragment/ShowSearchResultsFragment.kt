package com.vmenon.mpo.search.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.navigation.domain.NavigationDestination
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.domain.*
import com.vmenon.mpo.search.mvi.ShowSearchViewEvent
import com.vmenon.mpo.search.view.adapter.ShowSearchResultsAdapter
import com.vmenon.mpo.search.viewmodel.ShowSearchResultsViewModel
import com.vmenon.mpo.view.BaseFragment
import com.vmenon.mpo.view.LoadingStateHelper
import kotlinx.android.synthetic.main.fragment_show_search_results.*
import javax.inject.Inject

class ShowSearchResultsFragment : BaseFragment<SearchComponent>(),
    ShowSearchResultsAdapter.ShowSelectedListener,
    NavigationOrigin<SearchNavigationParams> by NavigationOrigin.from(SearchNavigationLocation) {

    @Inject
    lateinit var showDetailsDestination: NavigationDestination<ShowDetailsLocation>

    private val showSearchResultsViewModel: ShowSearchResultsViewModel by viewModel()

    private lateinit var adapter: ShowSearchResultsAdapter
    private lateinit var loadingStateHelper: LoadingStateHelper

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadingStateHelper = LoadingStateHelper(contentProgressBar, searchResultsContainer)
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        showList.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(requireContext())
        showList.layoutManager = layoutManager
        showList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter = ShowSearchResultsAdapter()
        adapter.setListener(this)
        showList.adapter = adapter

        val query = navigationController.getParams(this).query

        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(toolbar)
            activity.title = this.getString(R.string.show_search_title, query)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        showSearchResultsViewModel.send(ShowSearchViewEvent.SearchRequestedEvent(query))
        showSearchResultsViewModel.states().observe(viewLifecycleOwner, Observer { state ->
            state.unhandledContent()?.let { stateContent ->
                if (stateContent.loading) {
                    loadingStateHelper.showLoadingState()
                } else {
                    loadingStateHelper.showContentState()
                }

                if (stateContent.currentResults.isEmpty()) {
                    noShowsText.visibility = View.VISIBLE
                    showList.visibility = View.GONE
                } else {
                    showList.visibility = View.VISIBLE
                    noShowsText.visibility = View.GONE
                }

                if (stateContent.diffResult != null) {
                    adapter.update(stateContent.currentResults, stateContent.diffResult)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_search_results, container, false)
    }

    override fun onShowSelected(show: ShowSearchResultModel) {
        navigationController.navigate(this, showDetailsDestination, ShowDetailsParams(show.id))
    }

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showSearchResultsViewModel)
    }
}