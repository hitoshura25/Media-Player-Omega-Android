package com.vmenon.mpo.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.ShowModel
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.viewmodel.EpisodeDetailsViewModel
import kotlinx.android.synthetic.main.activity_episode_details.*

import java.text.DateFormat

import java.util.Date
import javax.inject.Inject

class EpisodeDetailsActivity : BaseDrawerCollapsingToolbarActivity() {

    @Inject
    lateinit var viewModel: EpisodeDetailsViewModel

    private var episodeId: Long = -1
    private var show: ShowModel? = null

    private lateinit var appBarImage: ImageView

    override val fabDrawableResource: Int
        get() = R.drawable.ic_play_arrow_white_48dp

    override val collapsedToolbarTitle: CharSequence
        get() = show?.details?.showName ?: ""

    override val expandedToolbarTitle: CharSequence
        get() = ""

    override val collapsiblePanelContentLayoutId: Int
        get() = R.layout.episode_details_panel_content

    override val layoutResourceId: Int
        get() = R.layout.activity_episode_details

    override val navMenuId: Int
        get() = R.id.nav_library

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBarImage = findViewById(R.id.appBarImage)
    }

    override fun onStart() {
        super.onStart()
        displayEpisode(intent)
    }

    override fun onFabClick() {
        val intent = Intent(this, MediaPlayerActivity::class.java)
        intent.putExtra(MediaPlayerActivity.EXTRA_EPISODE, episodeId)
        startActivity(intent)
    }

    private fun displayEpisode(intent: Intent) {
        episodeId = intent.getLongExtra(EXTRA_EPISODE, -1L)
        subscriptions.add(
            viewModel.getEpisodeDetails(episodeId)
                .subscribe(
                    { episodeWithShowDetails ->
                        episodeName.text = episodeWithShowDetails.episode.details.episodeName
                        @Suppress("DEPRECATION")
                        episodeDescription.text = Html.fromHtml(
                            episodeWithShowDetails.episode.details.description.replace(
                                "(<(//)img>)|(<img.+?>)".toRegex(),
                                ""
                            )
                        )
                        episodeDate.text = DateFormat.getDateInstance().format(
                            Date(episodeWithShowDetails.episode.details.published)
                        )
                        Glide.with(this@EpisodeDetailsActivity)
                            .load(episodeWithShowDetails.showDetails.showArtworkUrl)
                            .into(appBarImage)

                        episodeImage.visibility = View.VISIBLE
                        Glide.with(this@EpisodeDetailsActivity)
                            .load(episodeWithShowDetails.showDetails.showArtworkUrl).fitCenter()
                            .into(episodeImage)
                    },
                    { error ->

                    }
                )
        )
    }

    companion object {
        const val EXTRA_EPISODE = "extraEpisode"
    }
}
