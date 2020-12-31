package com.vmenon.mpo.library.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.vmenon.mpo.common.domain.ErrorState
import com.vmenon.mpo.common.domain.LoadingState
import com.vmenon.mpo.common.domain.SuccessState
import com.vmenon.mpo.library.R
import com.vmenon.mpo.library.di.dagger.LibraryComponent
import com.vmenon.mpo.library.di.dagger.LibraryComponentProvider
import com.vmenon.mpo.library.view.adapter.SubscriptionGalleryAdapter
import com.vmenon.mpo.library.viewmodel.SubscribedShowsViewModel
import com.vmenon.mpo.my_library.domain.SubscribedShowsLocation
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.navigation.domain.NoNavigationParams
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.subscribed_shows_fragment.*

class SubscribedShowsFragment : BaseFragment<LibraryComponent>(),
    NavigationOrigin<NoNavigationParams> by NavigationOrigin.from(SubscribedShowsLocation) {
    private val viewModel: SubscribedShowsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("${javaClass.name} onCreateView")

        return inflater.inflate(R.layout.subscribed_shows_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).let { activity ->
            activity.setSupportActionBar(toolbar)
            activity.setTitle(R.string.shows)
            activity.supportActionBar?.let { actionBar ->
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            }
        }
        showList.setHasFixedSize(true)
        showList.layoutManager = GridLayoutManager(context, 3)
        viewModel.subscribedShows().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                LoadingState -> {
                }
                is SuccessState -> {
                    showList.adapter = SubscriptionGalleryAdapter(result.result)
                }
                ErrorState -> {
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.let {
            inflater.inflate(R.menu.subscribed_shows_options_menu, menu)

            // Associate searchable configuration with the SearchView
            val menuItem = menu.findItem(R.id.search)
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            viewModel.searchForShows(query, this@SubscribedShowsFragment)
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean = false
                }
            )
        }
    }

    override fun setupComponent(context: Context) =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(this)
        component.inject(viewModel)
    }
}