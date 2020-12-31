package com.vmenon.mpo.library.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.my_library.domain.EpisodeDetailsLocation
import com.vmenon.mpo.my_library.domain.EpisodeDetailsParams
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_episode_details.*
import kotlinx.android.synthetic.main.fragment_episode_details.episodeDate
import kotlinx.android.synthetic.main.fragment_episode_details.episodeDescription
import kotlinx.android.synthetic.main.fragment_episode_details.episodeImage
import kotlinx.android.synthetic.main.fragment_episode_details.episodeName
import java.text.DateFormat
import java.util.*

class EpisodeDetailsFragment : BaseFragment<LibraryComponent>(),
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_details, container, false)
    }

    override fun setupComponent(context: Context): LibraryComponent =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val episodeId = navigationController.getParams(this).episodeId

        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(toolbar)
            activity.title = ""
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
        fab.setOnClickListener {
            viewModel.playEpisode(episodeId, this)
        }

        appbar.addOnOffsetChangedListener(this)

        viewModel.getEpisodeDetails(episodeId)
            .observe(
                viewLifecycleOwner,
                Observer { episodeWithShowDetails ->
                    when (episodeWithShowDetails) {
                        is SuccessState -> {
                            val episode = episodeWithShowDetails.result
                            show = episode.show
                            episodeName.text = episode.name
                            @Suppress("DEPRECATION")
                            episodeDescription.text = Html.fromHtml(
                                episode.description?.replace(
                                    "(<(//)img>)|(<img.+?>)".toRegex(),
                                    ""
                                ) ?: ""
                            )
                            episodeDate.text = DateFormat.getDateInstance().format(
                                Date(episode.published)
                            )
                            Glide.with(requireActivity())
                                .load(episode.show.artworkUrl)
                                .into(appBarImage)

                            if (episode.artworkUrl != null) {
                                episodeImage.visibility = View.VISIBLE
                                Glide.with(requireActivity())
                                    .load(episode.artworkUrl).fitCenter()
                                    .into(episodeImage)
                            } else {
                                episodeImage.visibility = View.GONE
                            }
                        }
                        LoadingState -> {
                        }
                        ErrorState -> {
                        }
                    }
                }
            )
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
}