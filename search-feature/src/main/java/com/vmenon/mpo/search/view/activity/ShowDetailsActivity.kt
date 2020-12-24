package com.vmenon.mpo.search.view.activity

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer

import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.vmenon.mpo.search.R

import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.view.adapter.EpisodesAdapter
import com.vmenon.mpo.search.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.view.LoadingStateHelper
import com.vmenon.mpo.view.activity.BaseDrawerCollapsingToolbarActivity
import kotlinx.android.synthetic.main.show_details_panel_content.*
import kotlinx.android.synthetic.main.show_details_content.*

class ShowDetailsActivity : BaseDrawerCollapsingToolbarActivity<SearchComponent, NoNavigationParams>(),
    AppBarLayout.OnOffsetChangedListener,
    EpisodesAdapter.EpisodeSelectedListener {

    private val showDetailsViewModel: ShowDetailsViewModel by viewModel()
    private var show: ShowSearchResultDetailsModel? = null

    private lateinit var loadingStateHelper: LoadingStateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingStateHelper = LoadingStateHelper(contentProgressBar, detailsContainer)

        val undoListener = View.OnClickListener { Log.d("MPO", "User clicked undo") }
        showDetailsViewModel.getShowDetails(intent.getLongExtra(EXTRA_SHOW, -1))
            .observe(this, Observer { showDetails ->
                when (showDetails) {
                    LoadingState -> loadingStateHelper.showLoadingState()
                    ErrorState -> TODO()
                    is SuccessState -> displayDetails(showDetails.result)
                }

            })

        showDetailsViewModel.showSubscribed().observe(this, Observer {
            Snackbar.make(
                detailsContainer, "You have subscribed to this show",
                Snackbar.LENGTH_LONG
            )
                .setAction("UNDO", undoListener)
                .show()
        })

        showDetailsViewModel.downloadQueued().observe(this, Observer {
            Snackbar.make(
                detailsContainer,
                "Episode download has been queued",
                Snackbar.LENGTH_LONG
            ).show()
        })
    }

    override fun onStop() {
        super.onStop()
        loadingStateHelper.reset()
    }

    override val fabDrawableResource: Int
        get() = R.drawable.ic_add_white_48dp
    override val collapsedToolbarTitle: CharSequence
        get() = show?.show?.name ?: ""
    override val expandedToolbarTitle: CharSequence
        get() = ""
    override val collapsiblePanelContentLayoutId: Int
        get() = R.layout.show_details_panel_content

    override fun onFabClick() {
        show?.let { showDetails ->
            showDetailsViewModel.subscribeToShow(showDetails)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.show_details_content
    override val navMenuId: Int
        get() = R.id.nav_home

    override fun onPlayEpisode(episode: ShowSearchResultEpisodeModel) {
        Snackbar.make(
            detailsContainer,
            "Not Implemented yet",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDownloadEpisode(episode: ShowSearchResultEpisodeModel) {
        show?.let { details ->
            showDetailsViewModel.queueDownload(details.show, episode)
        }
    }

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showDetailsViewModel)
    }

    private fun toggleSubscribeButton(subscribed: Boolean) {
        findViewById<View>(R.id.fab).visibility = if (subscribed) View.INVISIBLE else View.VISIBLE
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
        loadingStateHelper.showContentState()
        toggleSubscribeButton(showDetails.subscribed)
    }

    companion object {
        const val EXTRA_SHOW = "extraShow"
    }
}