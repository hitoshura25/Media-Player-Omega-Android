package com.vmenon.mpo.library.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer

import com.bumptech.glide.Glide
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.viewmodel.EpisodeDetailsViewModel
import com.vmenon.mpo.my_library.domain.ShowModel
import com.vmenon.mpo.navigation.domain.NavigationView
import com.vmenon.mpo.view.activity.BaseDrawerCollapsingToolbarActivity
import kotlinx.android.synthetic.main.activity_episode_details.*

import java.text.DateFormat

import java.util.Date

class EpisodeDetailsActivity : BaseDrawerCollapsingToolbarActivity<LibraryComponent>(),
    NavigationView {
    private val viewModel: EpisodeDetailsViewModel by viewModel()

    private var episodeId: Long = -1
    private var show: ShowModel? = null

    private lateinit var appBarImage: ImageView

    override val fabDrawableResource: Int
        get() = R.drawable.ic_play_arrow_white_48dp

    override val collapsedToolbarTitle: CharSequence
        get() = show?.name ?: ""

    override val expandedToolbarTitle: CharSequence
        get() = ""

    override val collapsiblePanelContentLayoutId: Int
        get() = R.layout.episode_details_panel_content

    override val layoutResourceId: Int
        get() = R.layout.activity_episode_details

    override val navMenuId: Int
        get() = R.id.nav_library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBarImage = findViewById(R.id.appBarImage)
    }

    override fun onStart() {
        super.onStart()
        displayEpisode(intent)
    }

    override fun onFabClick() {
        viewModel.playEpisode(episodeId, this)
    }

    private fun displayEpisode(intent: Intent) {
        episodeId = intent.getLongExtra(EXTRA_EPISODE, -1L)
        viewModel.getEpisodeDetails(episodeId)
            .observe(
                this,
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
                            Glide.with(this@EpisodeDetailsActivity)
                                .load(episode.show.artworkUrl)
                                .into(appBarImage)

                            if (episode.artworkUrl != null) {
                                episodeImage.visibility = View.VISIBLE
                                Glide.with(this@EpisodeDetailsActivity)
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

    companion object {
        const val EXTRA_EPISODE = "extraEpisode"
    }

    override fun setupComponent(context: Context): LibraryComponent =
        (context as LibraryComponentProvider).libraryComponent()


    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }
}