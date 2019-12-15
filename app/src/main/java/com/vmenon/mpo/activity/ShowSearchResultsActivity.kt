package com.vmenon.mpo.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.vmenon.mpo.R
import com.vmenon.mpo.adapter.ShowSearchResultsAdapter

import javax.inject.Inject

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.core.persistence.ShowSearchRepository
import com.vmenon.mpo.model.ShowSearchResultsModel
import io.reactivex.FlowableSubscriber
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.activity_show_search_results.*
import org.reactivestreams.Subscription

class ShowSearchResultsActivity : BaseActivity(), ShowSearchResultsAdapter.ShowSelectedListener {

    @Inject
    lateinit var showSearchRepository: ShowSearchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        setContentView(R.layout.activity_show_search_results)
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        showList.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this)
        showList.layoutManager = layoutManager
        showList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

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

            showSearchRepository.searchShows(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : ResourceSubscriber<List<ShowSearchResultsModel>>() {
                    override fun onNext(shows: List<ShowSearchResultsModel>?) {
                        shows?.let {
                            val adapter = ShowSearchResultsAdapter(shows)
                            adapter.setListener(this@ShowSearchResultsActivity)
                            showList.adapter = adapter
                        }
                    }

                    override fun onComplete() {
                    }

                    override fun onError(t: Throwable?) {
                    }
                })

        }
    }


}