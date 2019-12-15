package com.vmenon.mpo.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.vmenon.mpo.R
import com.vmenon.mpo.model.SubscribedShowModel
import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.model.EpisodeModel
import kotlinx.android.synthetic.main.activity_episode_details.*

import java.text.DateFormat

import java.util.Date

import javax.inject.Inject

class EpisodeDetailsActivity : BaseDrawerCollapsingToolbarActivity() {

    @Inject
    lateinit var repository: MPORepository

    private var episodeId: Long = -1
    private var show: SubscribedShowModel? = null

    private lateinit var appBarImage: ImageView

    override val fabDrawableResource: Int
        get() = R.drawable.ic_play_arrow_white_48dp

    override val collapsedToolbarTitle: CharSequence
        get() = show?.show?.name ?: ""

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
        episodeId = intent.getLongExtra(EXTRA_EPISODE, -1L)

        repository.fetchEpisode(episodeId, object: MPORepository.DataHandler<EpisodeModel> {
            override fun onDataReady(data: EpisodeModel) {
                episodeName.text = data.name
                @Suppress("DEPRECATION")
                episodeDescription.text = Html.fromHtml(
                    data.description.replace("(<(//)img>)|(<img.+?>)".toRegex(), "")
                )
                episodeDate.text = DateFormat.getDateInstance().format(
                    Date(data.published)
                )
                repository.getLiveShow(data.showId).observe(this@EpisodeDetailsActivity, Observer { show ->
                    this@EpisodeDetailsActivity.show = show
                    Glide.with(this@EpisodeDetailsActivity).load(show.show.artworkUrl)
                        .into(appBarImage)

                    episodeImage.visibility = View.VISIBLE
                    Glide.with(this@EpisodeDetailsActivity).load(show.show.artworkUrl).fitCenter()
                        .into(episodeImage)
                })
            }

        })



        appBarImage = findViewById(R.id.appBarImage)
    }

    override fun onFabClick() {
        val intent = Intent(this, MediaPlayerActivity::class.java)
        intent.putExtra(MediaPlayerActivity.EXTRA_EPISODE, episodeId)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_EPISODE = "extraEpisode"
    }
}
