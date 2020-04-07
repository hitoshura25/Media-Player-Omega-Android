package com.vmenon.mpo.view.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.ShowSearchResultsAdapter

import javax.inject.Inject

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.model.ShowSearchResultsModel
import com.vmenon.mpo.viewmodel.ShowSearchResultsViewModel
import kotlinx.android.synthetic.main.activity_show_search_results.*

class ShowSearchResultsActivity : BaseActivity(), ShowSearchResultsAdapter.ShowSelectedListener {

    @Inject
    lateinit var showSearchResultsViewModel: ShowSearchResultsViewModel

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_search_results)
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        showList.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this)
        showList.layoutManager = layoutManager
        showList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
        handleIntent(intent)
    }

    override fun onShowSelected(show: ShowSearchResultsModel) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.putExtra(ShowDetailsActivity.EXTRA_SHOW, show.id)
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            title = this.getString(R.string.show_search_title, query)

            subscriptions.add(
                showSearchResultsViewModel.searchShows(query).subscribe(
                    { shows ->
                        val adapter = ShowSearchResultsAdapter(shows)
                        adapter.setListener(this@ShowSearchResultsActivity)
                        showList.adapter = adapter
                    },
                    { error -> Log.w("MPO", "Error search for shows", error) }
                )
            )

        }
    }
}