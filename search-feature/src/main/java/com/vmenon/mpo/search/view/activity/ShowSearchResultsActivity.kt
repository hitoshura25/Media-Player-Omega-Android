package com.vmenon.mpo.search.view.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.view.adapter.ShowSearchResultsAdapter

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.domain.ShowSearchResultModel
import com.vmenon.mpo.search.view.adapter.diff.ShowSearchResultsDiff
import com.vmenon.mpo.search.viewmodel.ShowSearchResultsViewModel
import com.vmenon.mpo.view.LoadingStateHelper
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_show_search_results.*
import kotlinx.coroutines.launch

class ShowSearchResultsActivity : BaseDrawerActivity<SearchComponent>(),
    ShowSearchResultsAdapter.ShowSelectedListener {
    private val showSearchResultsViewModel: ShowSearchResultsViewModel by viewModel()

    private lateinit var adapter: ShowSearchResultsAdapter
    private lateinit var loadingStateHelper: LoadingStateHelper

    var searchResults: List<ShowSearchResultModel> = emptyList()

    override val layoutResourceId: Int
        get() = R.layout.activity_show_search_results

    override val navMenuId: Int
        get() = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingStateHelper = LoadingStateHelper(contentProgressBar, searchResultsContainer)
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        showList.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this)
        showList.layoutManager = layoutManager
        showList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter = ShowSearchResultsAdapter()
        adapter.setListener(this@ShowSearchResultsActivity)
        showList.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        handleIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        loadingStateHelper.reset()
    }

    override fun onShowSelected(show: ShowSearchResultModel) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.putExtra(ShowDetailsActivity.EXTRA_SHOW, show.id)
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.let { query ->
                title = this.getString(R.string.show_search_title, query)
                showSearchResultsViewModel.searchShows(query)
                showSearchResultsViewModel.getShowSearchResultsForTerm().observe(this,
                    Observer { results ->
                        when (results) {
                            LoadingState -> {
                                loadingStateHelper.showLoadingState()
                            }
                            ErrorState -> TODO()
                            is SuccessState -> {
                                lifecycleScope.launch {
                                    val diff = showSearchResultsViewModel.calculateDiff(
                                        results.result,
                                        ShowSearchResultsDiff(searchResults, results.result)
                                    )
                                    this@ShowSearchResultsActivity.searchResults = diff.first
                                    adapter.update(
                                        this@ShowSearchResultsActivity.searchResults,
                                        diff.second
                                    )
                                    if (searchResults.isEmpty()) {
                                        noShowsText.visibility = View.VISIBLE
                                        showList.visibility = View.GONE
                                    } else {
                                        showList.visibility = View.VISIBLE
                                        noShowsText.visibility = View.GONE
                                    }
                                    loadingStateHelper.showContentState()
                                }                            }
                        }

                    }
                )
            }
        }
    }

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showSearchResultsViewModel)
    }
}