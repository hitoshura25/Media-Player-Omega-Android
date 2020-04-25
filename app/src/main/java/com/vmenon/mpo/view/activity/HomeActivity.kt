package com.vmenon.mpo.view.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager

import android.util.Log
import android.view.Menu

import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.SubscriptionGalleryAdapter
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity() {

    @Inject
    lateinit var viewModel: HomeViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val navMenuId: Int
        get() = R.id.nav_home

    override val isRootActivity: Boolean
        get() = true

    override fun inject(appComponent: AppComponent) {
        appComponent.activityComponent().create().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.shows)
        showList.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.orientation = GridLayoutManager.HORIZONTAL
        showList.layoutManager = GridLayoutManager(this, 3)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )

        return true
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            viewModel.subscribedShows()
                .subscribe(
                    { shows ->
                        Log.d("MPO", "Got " + shows.size + " shows")
                        val adapter = SubscriptionGalleryAdapter(shows)
                        adapter.setHasStableIds(true)
                        showList.adapter = adapter
                    },
                    { error ->

                    }
                )
        )
    }
}
