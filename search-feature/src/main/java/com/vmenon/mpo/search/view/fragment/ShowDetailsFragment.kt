package com.vmenon.mpo.search.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.domain.ShowDetailsLocation
import com.vmenon.mpo.search.domain.ShowDetailsParams
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.view.adapter.EpisodesAdapter
import com.vmenon.mpo.search.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.view.BaseFragment
import com.vmenon.mpo.view.LoadingStateHelper
import kotlinx.android.synthetic.main.fragment_show_details.*
import kotlinx.android.synthetic.main.fragment_show_details.appbar
import kotlinx.android.synthetic.main.fragment_show_details.contentProgressBar
import kotlinx.android.synthetic.main.fragment_show_details.detailsContainer
import kotlinx.android.synthetic.main.fragment_show_details.toolbar

class ShowDetailsFragment : BaseFragment<SearchComponent>(), AppBarLayout.OnOffsetChangedListener,
    NavigationOrigin<ShowDetailsParams> by NavigationOrigin.from(ShowDetailsLocation),
    EpisodesAdapter.EpisodeSelectedListener {
    private lateinit var loadingStateHelper: LoadingStateHelper

    private var collapsed = false
    private var scrollRange = -1

    private val collapsedToolbarTitle: CharSequence
        get() = show?.show?.name ?: ""
    private val expandedToolbarTitle: CharSequence
        get() = ""

    private val showDetailsViewModel: ShowDetailsViewModel by viewModel()
    private var show: ShowSearchResultDetailsModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_details, container, false)
    }

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showDetailsViewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(toolbar)
            activity.title = ""
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        fab.setImageResource(R.drawable.ic_add_white_48dp)
        fab.setOnClickListener {
            show?.let { showDetails ->
                showDetailsViewModel.subscribeToShow(showDetails)
            }
        }

        appbar.addOnOffsetChangedListener(this)

        loadingStateHelper = LoadingStateHelper(contentProgressBar, detailsContainer)

        val undoListener = View.OnClickListener { Log.d("MPO", "User clicked undo") }
        val showId = navigationController.getParams(this).showSearchResultId
        showDetailsViewModel.getShowDetails(showId)
            .observe(viewLifecycleOwner, Observer { showDetails ->
                when (showDetails) {
                    LoadingState -> loadingStateHelper.showLoadingState()
                    ErrorState -> {

                    }
                    is SuccessState -> displayDetails(showDetails.result)
                }

            })

        showDetailsViewModel.showSubscribed().observe(viewLifecycleOwner, Observer {
            Snackbar.make(
                detailsContainer, "You have subscribed to this show",
                Snackbar.LENGTH_LONG
            )
                .setAction("UNDO", undoListener)
                .show()
        })

        showDetailsViewModel.downloadQueued().observe(viewLifecycleOwner, Observer {
            Snackbar.make(
                detailsContainer,
                "Episode download has been queued",
                Snackbar.LENGTH_LONG
            ).show()
        })
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            collapsing_toolbar.title = collapsedToolbarTitle
            collapsed = true
        } else if (collapsed) {
            collapsing_toolbar.title = expandedToolbarTitle
            collapsed = false
        }
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
            showDetailsViewModel.queueDownload(details.show, episode)
        }
    }

    private fun displayDetails(showDetails: ShowSearchResultDetailsModel) {
        show = showDetails
        @Suppress("DEPRECATION")
        showDescription.text = Html.fromHtml(showDetails.show.description)
        Glide.with(requireActivity()).load(showDetails.show.artworkUrl).fitCenter().into(showImage)

        episodesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        episodesList.layoutManager = layoutManager
        episodesList.adapter = EpisodesAdapter(showDetails).apply {
            setListener(this@ShowDetailsFragment)
        }
        loadingStateHelper.showContentState()
        toggleSubscribeButton(showDetails.subscribed)
    }

    private fun toggleSubscribeButton(subscribed: Boolean) {
        fab.visibility = if (subscribed) View.INVISIBLE else View.VISIBLE
    }
}