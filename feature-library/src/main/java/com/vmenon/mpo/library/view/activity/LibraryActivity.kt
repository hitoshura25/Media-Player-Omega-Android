package com.vmenon.mpo.library.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.model.EpisodeModel

import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.view.adapter.LibraryAdapter
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import com.vmenon.mpo.view.activity.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : BaseDrawerActivity<LibraryComponent>(),
    LibraryAdapter.LibrarySelectedListener {
    private val viewModel: LibraryViewModel by viewModel()

    override val layoutResourceId: Int
        get() = R.layout.activity_library

    override val navMenuId: Int
        get() = R.id.nav_library

    override val isRootActivity: Boolean
        get() = true

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

    override fun setupComponent(context: Context): LibraryComponent =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }
}