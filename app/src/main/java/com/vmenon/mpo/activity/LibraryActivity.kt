package com.vmenon.mpo.activity

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.vmenon.mpo.R
import com.vmenon.mpo.adapter.EpisodesAdapter
import com.vmenon.mpo.adapter.LibraryAdapter
import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.core.persistence.MPORepository
import kotlinx.android.synthetic.main.activity_library.*

import org.parceler.Parcels

import javax.inject.Inject

class LibraryActivity : BaseDrawerActivity(), EpisodesAdapter.EpisodeSelectedListener {

    @Inject
    lateinit var mpoRepository: MPORepository

    override val layoutResourceId: Int
        get() = R.layout.activity_library

    override val navMenuId: Int
        get() = R.id.nav_library

    override val isRootActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        val layoutManager = LinearLayoutManager(this)
        libraryList.setHasFixedSize(true)
        libraryList.layoutManager = layoutManager
        libraryList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        mpoRepository.allEpisodes.observe(this, Observer { episodes ->
            val adapter = LibraryAdapter(episodes)
            adapter.setListener(this@LibraryActivity)
            libraryList.adapter = adapter
        })
    }

    override fun onEpisodeSelected(episode: Episode?) {
        val intent = Intent(this, EpisodeDetailsActivity::class.java)
        intent.putExtra(EpisodeDetailsActivity.EXTRA_EPISODE, Parcels.wrap<Episode>(episode))
        startActivity(intent)
    }
}