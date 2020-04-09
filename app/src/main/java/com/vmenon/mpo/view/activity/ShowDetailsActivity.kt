package com.vmenon.mpo.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver

import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.EpisodesAdapter

import javax.inject.Inject

import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.model.EpisodeModel
import com.vmenon.mpo.model.ShowDetailsAndEpisodesModel
import com.vmenon.mpo.viewmodel.ShowDetailsViewModel
import kotlinx.android.synthetic.main.activity_show_details.*
import kotlinx.android.synthetic.main.show_details_container.*

class ShowDetailsActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener,
    EpisodesAdapter.EpisodeSelectedListener {

    @Inject
    lateinit var showDetailsViewModel: ShowDetailsViewModel

    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private var show: ShowDetailsAndEpisodesModel? = null

    private var collapsed = false
    private var scrollRange = -1

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_details)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)

        nav_view.setNavigationItemSelectedListener { menuItem ->
            if (R.id.nav_downloads == menuItem.itemId) {
                val intent = Intent(
                    this@ShowDetailsActivity,
                    DownloadsActivity::class.java
                )
                startActivity(intent)
            }

            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            showDetailsViewModel.getShowDetails(intent.getLongExtra(EXTRA_SHOW, -1))
                .subscribe(
                    { showDetails ->
                        displayDetails(showDetails)
                    },
                    { error -> Log.w("MPO", "Error search for shows", error) }
                )
        )
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbar.title = show?.showDetails?.name
            collapsed = true
        } else if (collapsed) {
            collapsingToolbar.title = ""
            collapsed = false
        }
    }

    private fun displayDetails(showDetails: ShowDetailsAndEpisodesModel) {
        show = showDetails
        @Suppress("DEPRECATION")
        showDescription.text = Html.fromHtml(showDetails.showDescription)
        Glide.with(this).load(showDetails.showDetails.artworkUrl).fitCenter().into(showImage)

        episodesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        episodesList.layoutManager = layoutManager
        episodesList.adapter = EpisodesAdapter(
            showDetails.showDetails,
            showDetails.episodes.map {
                EpisodeModel(
                    name = it.name,
                    description = it.description,
                    published = it.published,
                    type = it.type,
                    downloadUrl = it.downloadUrl,
                    length = it.length,
                    artworkUrl = it.artworkUrl,
                    showId = 0L,
                    filename = ""
                )
            }
        ).apply { setListener(this@ShowDetailsActivity) }

        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    nestedScrollView.scrollY = 0
                    @Suppress("DEPRECATION")
                    nestedScrollView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            })

        val undoListener = View.OnClickListener { Log.d("MPO", "User clicked undo") }

        subscribeButton.setOnClickListener {
            subscriptions.add(
                showDetailsViewModel.subscribeToShow(showDetails.showDetails).ignoreElement()
                    .subscribe(
                        {
                            Snackbar.make(
                                detailsContainer, "You have subscribed to this show",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("UNDO", undoListener)
                                .show()

                        },
                        { error -> error.printStackTrace() }
                    )
            )
        }

    }

    companion object {
        const val EXTRA_SHOW = "extraShow"
    }

    override fun onEpisodeSelected(episode: EpisodeModel) {

    }

    override fun onDownloadEpisode(episode: EpisodeModel) {
        show?.let {
            showDetailsViewModel.queueDownload(it.showDetails, episode)
        }
    }
}