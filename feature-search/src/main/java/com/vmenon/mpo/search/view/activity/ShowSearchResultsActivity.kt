package com.vmenon.mpo.search.view.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.view.adapter.ShowSearchResultsAdapter

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.model.ShowSearchResultModel
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.view.adapter.diff.ShowSearchResultsDiff
import com.vmenon.mpo.search.viewmodel.ShowSearchResultsViewModel
import com.vmenon.mpo.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_show_search_results.*

class ShowSearchResultsActivity : BaseActivity(), ShowSearchResultsAdapter.ShowSelectedListener {
    private val showSearchResultsViewModel by lazy {
        viewModel() as ShowSearchResultsViewModel
    }

    lateinit var adapter: ShowSearchResultsAdapter

    var searchResults: List<ShowSearchResultModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as SearchComponentProvider).searchComponent().apply {
            inject(this@ShowSearchResultsActivity)
            inject(showSearchResultsViewModel)
        }

        setContentView(R.layout.activity_show_search_results)
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

    override fun onShowSelected(show: ShowSearchResultModel) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.putExtra(ShowDetailsActivity.EXTRA_SHOW, show.id)
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.let { query ->
                title = this.getString(R.string.show_search_title, query)

                subscriptions.add(
                    showSearchResultsViewModel.searchShows(query)
                        .subscribe(
                            {

                            },
                            {

                            }
                        )
                )

                subscriptions.add(
                    showSearchResultsViewModel.getShowSearchResultsForTerm(query)
                        .flatMapSingle {
                            showSearchResultsViewModel.getDiff(
                                it,
                                ShowSearchResultsDiff(searchResults, it)
                            )
                        }
                        .subscribe(
                            { showsAndDiff ->
                                this.searchResults = showsAndDiff.first
                                adapter.update(this.searchResults, showsAndDiff.second)
                            },
                            { error -> Log.w("MPO", "Error search for shows", error) }
                        )
                )
            }
        }
    }
}