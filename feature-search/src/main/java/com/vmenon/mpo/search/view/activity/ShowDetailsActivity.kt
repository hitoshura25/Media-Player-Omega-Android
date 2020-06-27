package com.vmenon.mpo.search.view.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener

import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.vmenon.mpo.search.R

import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.model.ShowSearchResultDetailsModel
import com.vmenon.mpo.model.ShowSearchResultEpisodeModel
import com.vmenon.mpo.navigation.NavigationController
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.view.adapter.EpisodesAdapter
import com.vmenon.mpo.search.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.view.LoadingStateHelper
import com.vmenon.mpo.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_show_details.*
import kotlinx.android.synthetic.main.show_details_container.*

class ShowDetailsActivity : BaseActivity<SearchComponent>(), AppBarLayout.OnOffsetChangedListener,
    EpisodesAdapter.EpisodeSelectedListener {

    private val showDetailsViewModel: ShowDetailsViewModel by viewModel()
    private var show: ShowSearchResultDetailsModel? = null

    private var collapsed = false
    private var scrollRange = -1

    private lateinit var loadingStateHelper: LoadingStateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)
        loadingStateHelper = LoadingStateHelper(contentProgressBar, detailsContainer)
        loadingStateHelper.showLoadingState()

        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener { menuItem ->
            val location = when (menuItem.itemId) {
                R.id.nav_downloads -> NavigationController.Location.DOWNLOADS
                R.id.nav_library -> NavigationController.Location.LIBRARY
                else -> NavigationController.Location.HOME
            }
            navigationController.onNavigationSelected(location, this, null)
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        val showSearchResultId = intent.getLongExtra(EXTRA_SHOW, -1)
        subscriptions.add(
            showDetailsViewModel.getShowDetails(showSearchResultId)
                .subscribe(
                    { showDetails ->
                        displayDetails(showDetails)
                    },
                    { error ->
                        Log.w("MPO", "Error getting show details for id $showSearchResultId", error)
                        Snackbar.make(
                            detailsContainer,
                            "There was a problem getting the details for this show",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                )
        )
    }

    override fun onStop() {
        super.onStop()
        loadingStateHelper.reset()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            collapsing_toolbar.title = show?.show?.name
            collapsed = true
        } else if (collapsed) {
            collapsing_toolbar.title = ""
            collapsed = false
        }
    }

    private fun displayDetails(showDetails: ShowSearchResultDetailsModel) {
        show = showDetails
        @Suppress("DEPRECATION")
        showDescription.text = Html.fromHtml(showDetails.show.description)
        Glide.with(this).load(showDetails.show.artworkUrl).fitCenter().into(showImage)

        episodesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        episodesList.layoutManager = layoutManager
        episodesList.adapter = EpisodesAdapter(showDetails).apply {
            setListener(this@ShowDetailsActivity)
        }

        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    nestedScrollView.scrollY = 0
                    @Suppress("DEPRECATION")
                    nestedScrollView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            })

        subscribeButton.setOnClickListener {
            subscriptions.add(
                showDetailsViewModel.subscribeToShow(showDetails).ignoreElement()
                    .subscribe(
                        {
                            Snackbar.make(
                                detailsContainer,
                                "You have subscribed to this show",
                                Snackbar.LENGTH_LONG
                            ).show()
                        },
                        { error -> error.printStackTrace() }
                    )
            )
        }
        loadingStateHelper.showContentState()
        subscribeButton.visibility = View.VISIBLE
    }

    override fun onPlayEpisode(episode: ShowSearchResultEpisodeModel) {
        Snackbar.make(
            detailsContainer,
            "Not Implemented yet",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDownloadEpisode(episode: ShowSearchResultEpisodeModel) {
        show?.let { details ->
            subscriptions.add(
                showDetailsViewModel.queueDownload(details.show, episode)
                    .ignoreElement()
                    .subscribe {
                        Snackbar.make(
                            detailsContainer,
                            "Episode download has been queued",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            )
        }
    }

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showDetailsViewModel)
    }

    companion object {
        const val EXTRA_SHOW = "extraShow"
    }
}