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
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.search.R
import com.vmenon.mpo.search.databinding.FragmentShowDetailsBinding
import com.vmenon.mpo.search.di.dagger.SearchComponent
import com.vmenon.mpo.search.di.dagger.SearchComponentProvider
import com.vmenon.mpo.search.domain.ShowDetailsLocation
import com.vmenon.mpo.search.domain.ShowDetailsParams
import com.vmenon.mpo.search.domain.ShowSearchResultDetailsModel
import com.vmenon.mpo.search.domain.ShowSearchResultEpisodeModel
import com.vmenon.mpo.search.mvi.ShowDetailsViewEffect
import com.vmenon.mpo.search.mvi.ShowDetailsViewEvent
import com.vmenon.mpo.search.view.adapter.EpisodesAdapter
import com.vmenon.mpo.search.viewmodel.ShowDetailsViewModel
import com.vmenon.mpo.view.BaseViewBindingFragment
import com.vmenon.mpo.view.LoadingStateHelper

class ShowDetailsFragment : BaseViewBindingFragment<SearchComponent, FragmentShowDetailsBinding>(),
    AppBarLayout.OnOffsetChangedListener,
    NavigationOrigin<ShowDetailsParams> by NavigationOrigin.from(ShowDetailsLocation) {
    private lateinit var loadingStateHelper: LoadingStateHelper

    private var collapsed = false
    private var scrollRange = -1

    private var collapsedToolbarTitle: CharSequence = ""
    private val expandedToolbarTitle: CharSequence = ""

    private val showDetailsViewModel: ShowDetailsViewModel by viewModel()

    override fun setupComponent(context: Context): SearchComponent =
        (context as SearchComponentProvider).searchComponent()

    override fun inject(component: SearchComponent) {
        component.inject(this)
        component.inject(showDetailsViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(binding.toolbar)
            activity.title = ""
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.fab.setImageResource(R.drawable.ic_add_white_48dp)
        binding.appbar.addOnOffsetChangedListener(this)

        loadingStateHelper = LoadingStateHelper.switchWithContent(
            loadingView = binding.contentProgressBar,
            contentView = binding.detailsContainer
        )

        val undoListener = View.OnClickListener { Log.d("MPO", "User clicked undo") }
        val showId = navigationController.getParams(this).showSearchResultId
        showDetailsViewModel.send(ShowDetailsViewEvent.LoadShowDetailsEvent(showId))
        showDetailsViewModel.states().observe(viewLifecycleOwner, Observer { event ->
            event.unhandledContent()?.let { state ->
                if (state.loading) {
                    loadingStateHelper.showLoadingState()
                } else {
                    loadingStateHelper.showContentState()
                }
                if (state.showDetails != null) {
                    collapsedToolbarTitle = state.showDetails.show.name
                    displayDetails(state.showDetails)
                    binding.fab.setOnClickListener {
                        showDetailsViewModel.send(
                            ShowDetailsViewEvent.SubscribeToShowEvent(state.showDetails)
                        )
                    }
                } else {
                    binding.fab.setOnClickListener(null)
                    collapsedToolbarTitle = ""
                }
            }
        })
        showDetailsViewModel.effects().observe(viewLifecycleOwner, Observer { event ->
            event.unhandledContent()?.let { effect ->
                when (effect) {
                    is ShowDetailsViewEffect.ShowSubscribedViewEffect -> {
                        Snackbar.make(
                            binding.detailsContainer, "You have subscribed to this show",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("UNDO", undoListener)
                            .show()
                    }
                    is ShowDetailsViewEffect.DownloadQueuedViewEffect -> {
                        Snackbar.make(
                            binding.detailsContainer,
                            "Episode download has been queued",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            binding.collapsingToolbar.title = collapsedToolbarTitle
            collapsed = true
        } else if (collapsed) {
            binding.collapsingToolbar.title = expandedToolbarTitle
            collapsed = false
        }
    }

    private fun displayDetails(showDetails: ShowSearchResultDetailsModel) {
        @Suppress("DEPRECATION")
        binding.showDescription.text = Html.fromHtml(showDetails.show.description)
        Glide.with(requireActivity()).load(showDetails.show.artworkUrl).fitCenter()
            .into(binding.showImage)

        binding.episodesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.episodesList.layoutManager = layoutManager
        binding.episodesList.adapter = EpisodesAdapter(showDetails).apply {
            setListener(object : EpisodesAdapter.EpisodeSelectedListener {
                override fun onPlayEpisode(episode: ShowSearchResultEpisodeModel) {
                    Snackbar.make(
                        binding.detailsContainer,
                        "Not Implemented yet",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onDownloadEpisode(episode: ShowSearchResultEpisodeModel) {
                    showDetailsViewModel.send(
                        ShowDetailsViewEvent.QueueDownloadEvent(
                            showDetails.show,
                            episode
                        )
                    )
                }
            })
        }
        loadingStateHelper.showContentState()
        toggleSubscribeButton(showDetails.subscribed)
    }

    private fun toggleSubscribeButton(subscribed: Boolean) {
        binding.fab.visibility = if (subscribed) View.INVISIBLE else View.VISIBLE
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentShowDetailsBinding =
        FragmentShowDetailsBinding.inflate(inflater, container, false)
}