package com.vmenon.mpo.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenom.mpo.model.EpisodeModel

import com.vmenon.mpo.R
import com.vmenon.mpo.view.adapter.LibraryAdapter
import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.viewmodel.LibraryViewModel
import kotlinx.android.synthetic.main.activity_library.*
import javax.inject.Inject

class LibraryActivity : BaseDrawerActivity(), LibraryAdapter.LibrarySelectedListener {

    @Inject
    lateinit var viewModel: LibraryViewModel

    override val layoutResourceId: Int
        get() = R.layout.activity_library

    override val navMenuId: Int
        get() = R.id.nav_library

    override val isRootActivity: Boolean
        get() = true

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutManager = LinearLayoutManager(this)
        libraryList.setHasFixedSize(true)
        libraryList.layoutManager = layoutManager
        libraryList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            viewModel.allEpisodes().subscribe(
                { episodes ->
                    val adapter = LibraryAdapter(episodes)
                    adapter.setListener(this@LibraryActivity)
                    libraryList.adapter = adapter
                },
                { error -> }
            )
        )
    }

    override fun onEpisodeSelected(episodeWithShowDetails: EpisodeModel) {
        val intent = Intent(this, EpisodeDetailsActivity::class.java)
        intent.putExtra(EpisodeDetailsActivity.EXTRA_EPISODE, episodeWithShowDetails.id)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}