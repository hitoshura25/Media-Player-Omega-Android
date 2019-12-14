package com.vmenon.mpo.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.core.persistence.MPORepository
import kotlinx.android.synthetic.main.activity_episode_details.*

import org.parceler.Parcels
import java.text.DateFormat

import java.util.Date

import javax.inject.Inject

class EpisodeDetailsActivity : BaseDrawerCollapsingToolbarActivity() {

    @Inject
    lateinit var repository: MPORepository

    private lateinit var episode: Episode
    private var show: Show? = null

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
        appComponent.inject(this)
        episode = Parcels.unwrap<Episode>(intent.getParcelableExtra<Parcelable>(EXTRA_EPISODE))

        episodeName.text = episode.name
        @Suppress("DEPRECATION")
        episodeDescription.text = Html.fromHtml(
            episode.description?.replace("(<(//)img>)|(<img.+?>)".toRegex(), "") ?: ""
        )
        episodeDate.text = DateFormat.getDateInstance().format(
            Date(episode.published)
        )
        repository.getLiveShow(episode.showId).observe(this, Observer { show ->
            this@EpisodeDetailsActivity.show = show
            Glide.with(this@EpisodeDetailsActivity).load(show!!.artworkUrl)
                .into(appBarImage)

            episodeImage.visibility = View.GONE
            episode.artworkUrl?.let { artworkUrl ->
                if (artworkUrl != show.artworkUrl) {
                    episodeImage.visibility = View.VISIBLE
                    Glide.with(this@EpisodeDetailsActivity).load(artworkUrl).fitCenter()
                        .into(episodeImage)
                }
            }
        })

        appBarImage = findViewById(R.id.appBarImage)
    }

    override fun onFabClick() {
        val intent = Intent(this, MediaPlayerActivity::class.java)
        intent.putExtra(MediaPlayerActivity.EXTRA_EPISODE, Parcels.wrap<Episode>(episode))
        startActivity(intent)
    }

    companion object {
        const val EXTRA_EPISODE = "extraEpisode"
    }
}
