package com.vmenon.mpo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.vmenon.mpo.adapter.EpisodesAdapter
import com.vmenon.mpo.api.Show
import com.vmenon.mpo.api.ShowDetails
import com.vmenon.mpo.core.persistence.MPORepository
import com.vmenon.mpo.service.MediaPlayerOmegaService

import org.parceler.Parcels

import javax.inject.Inject

import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_show_details.*
import kotlinx.android.synthetic.main.show_details_container.*

class ShowDetailsActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener {

    @Inject
    lateinit var service: MediaPlayerOmegaService

    @Inject
    lateinit var mpoRepository: MPORepository

    private var collapsingToolbar: CollapsingToolbarLayout? = null
    private var show: Show? = null

    private var collapsed = false
    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        setContentView(R.layout.activity_show_details)

        show = Parcels.unwrap<Show>(intent.getParcelableExtra<Parcelable>(EXTRA_SHOW))

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

        if (savedInstanceState == null) {
            service.getPodcastDetails(show!!.feedUrl!!, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showDetails -> displayDetails(showDetails) }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbar!!.title = show!!.name
            collapsed = true
        } else if (collapsed) {
            collapsingToolbar!!.title = ""
            collapsed = false
        }
    }

    private fun displayDetails(showDetails: ShowDetails) {
        @Suppress("DEPRECATION")
        showDescription.text = Html.fromHtml(showDetails.description)
        Glide.with(this).load(showDetails.imageUrl).fitCenter().into(showImage)

        episodesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        episodesList.layoutManager = layoutManager
        episodesList.adapter = EpisodesAdapter(show!!, showDetails.episodes)

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
            mpoRepository.save(show!!)
            Snackbar.make(
                detailsContainer!!, "You have subscribed to this show",
                Snackbar.LENGTH_LONG
            )
                .setAction("UNDO", undoListener)
                .show()
        }
    }

    companion object {
        const val EXTRA_SHOW = "extraShow"
    }
}