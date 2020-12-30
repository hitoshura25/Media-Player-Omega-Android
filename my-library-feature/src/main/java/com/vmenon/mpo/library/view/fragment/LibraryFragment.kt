package com.vmenon.mpo.library.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.view.activity.EpisodeDetailsActivity
import com.vmenon.mpo.library.view.adapter.LibraryAdapter
import com.vmenon.mpo.library.viewmodel.LibraryViewModel
import com.vmenon.mpo.my_library.domain.EpisodeModel
import com.vmenon.mpo.my_library.domain.MyLibraryNavigationLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.fragment_library.toolbar

class LibraryFragment : BaseFragment<LibraryComponent>(), LibraryAdapter.LibrarySelectedListener,
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(MyLibraryNavigationLocation) {
    private val viewModel: LibraryViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(toolbar)
            activity.setTitle(R.string.library)
            activity.supportActionBar?.let { actionBar ->
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            }
        }

        val layoutManager = LinearLayoutManager(context)
        libraryList.setHasFixedSize(true)
        libraryList.layoutManager = layoutManager
        libraryList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.allEpisodes().observe(
            viewLifecycleOwner,
            Observer { episodes ->
                when (episodes) {
                    is SuccessState -> {
                        val adapter = LibraryAdapter(episodes.result)
                        adapter.setListener(this@LibraryFragment)
                        libraryList.adapter = adapter
                    }
                    LoadingState -> {
                    }
                    ErrorState -> {
                    }
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("${javaClass.name} onCreateView")

        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun setupComponent(context: Context) =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun onEpisodeSelected(episodeWithShowDetails: EpisodeModel) {
        val intent = Intent(context, EpisodeDetailsActivity::class.java)
        intent.putExtra(EpisodeDetailsActivity.EXTRA_EPISODE, episodeWithShowDetails.id)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}