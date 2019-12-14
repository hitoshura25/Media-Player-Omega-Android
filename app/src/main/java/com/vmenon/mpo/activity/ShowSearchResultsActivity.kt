package com.vmenon.mpo.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.vmenon.mpo.R
import com.vmenon.mpo.adapter.ShowSearchResultsAdapter
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.service.MediaPlayerOmegaService

import org.parceler.Parcels
import javax.inject.Inject

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_show_search_results.*

class ShowSearchResultsActivity : BaseActivity(), ShowSearchResultsAdapter.ShowSelectedListener {

    @Inject
    lateinit var service: MediaPlayerOmegaService

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

    override fun onShowSelected(show: Show?) {
        val intent = Intent(this, ShowDetailsActivity::class.java)
        intent.putExtra(ShowDetailsActivity.EXTRA_SHOW, Parcels.wrap<Show>(show))
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            title = this.getString(R.string.show_search_title, query)

            service.searchPodcasts(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith<Observer<List<Show>>>(object : Observer<List<Show>> {
                        override fun onSubscribe(@NonNull d: Disposable) {

                        }

                        override fun onNext(@NonNull shows: List<Show>) {
                            val adapter = ShowSearchResultsAdapter(shows)
                            adapter.setListener(this@ShowSearchResultsActivity)
                            showList!!.adapter = adapter
                        }

                        override fun onError(@NonNull e: Throwable) {
                            Log.w("MPO", "Error search for shows", e)
                        }

                        override fun onComplete() {

                        }
                    })

        }
    }


}