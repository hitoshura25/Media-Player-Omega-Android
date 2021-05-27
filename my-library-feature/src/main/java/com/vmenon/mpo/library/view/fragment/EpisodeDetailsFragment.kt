package com.vmenon.mpo.library.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.databinding.FragmentEpisodeDetailsBinding
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.my_library.domain.EpisodeDetailsLocation
import com.vmenon.mpo.my_library.domain.EpisodeDetailsParams
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.view.BaseViewBindingFragment
import java.text.DateFormat
import java.util.*

class EpisodeDetailsFragment :
    BaseViewBindingFragment<LibraryComponent, FragmentEpisodeDetailsBinding>(),
    NavigationOrigin<EpisodeDetailsParams> by NavigationOrigin.from(EpisodeDetailsLocation),
    AppBarLayout.OnOffsetChangedListener {

    private val viewModel: EpisodeDetailsViewModel by viewModel()

    private var show: ShowModel? = null

    private var collapsed = false
    private var scrollRange = -1

    private val collapsedToolbarTitle: CharSequence
        get() = show?.name ?: ""

    private val expandedToolbarTitle: CharSequence
        get() = ""

    override fun setupComponent(context: Context): LibraryComponent =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val episodeId = navigationController.getParams(this).episodeId

        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(binding.toolbar)
            activity.title = ""
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
        binding.fab.setOnClickListener {
            viewModel.playEpisode(episodeId, this)
        }

        binding.appbar.addOnOffsetChangedListener(this)

        viewModel.getEpisodeDetails(episodeId)
            .observe(
                viewLifecycleOwner,
                { episodeWithShowDetails ->
                    when (episodeWithShowDetails) {
                        is SuccessState -> {
                            val episode = episodeWithShowDetails.result
                            show = episode.show
                            binding.episodeName.text = episode.name
                            @Suppress("DEPRECATION")
                            binding.episodeDescription.text = Html.fromHtml(
                                episode.description?.replace(
                                    "(<(//)img>)|(<img.+?>)".toRegex(),
                                    ""
                                ) ?: ""
                            )
                            binding.episodeDate.text = DateFormat.getDateInstance().format(
                                Date(episode.published)
                            )
                            Glide.with(requireActivity())
                                .load(episode.show.artworkUrl)
                                .into(binding.appBarImage)

                            if (episode.artworkUrl != null) {
                                binding.episodeImage.visibility = View.VISIBLE
                                Glide.with(requireActivity())
                                    .load(episode.artworkUrl).fitCenter()
                                    .into(binding.episodeImage)
                            } else {
                                binding.episodeImage.visibility = View.GONE
                            }
                        }
                        LoadingState -> {
                        }
                        ErrorState -> {
                        }
                        else -> {}
                    }
                }
            )
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

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEpisodeDetailsBinding =
        FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
}