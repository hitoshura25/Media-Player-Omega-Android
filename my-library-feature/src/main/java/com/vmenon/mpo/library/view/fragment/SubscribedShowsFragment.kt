package com.vmenon.mpo.library.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.vmenon.mpo.view.BaseFragment
import kotlinx.android.synthetic.main.subscribed_shows_fragment.*

class SubscribedShowsFragment : BaseFragment<LibraryComponent>() {

    companion object {
        fun newInstance() = SubscribedShowsFragment()
    }

    private val viewModel: SubscribedShowsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.subscribed_shows_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showList.setHasFixedSize(true)
        showList.layoutManager = GridLayoutManager(context, 3)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.shows)

        viewModel.subscribedShows.observe(this, Observer { result ->
            when (result) {
                LoadingState -> {}
                is SuccessState -> {
                    showList.adapter = SubscriptionGalleryAdapter(result.result)
                }
                ErrorState -> {
                }
            }
        })
    }

    override fun setupComponent(context: Context) =
        (context as LibraryComponentProvider).libraryComponent()

    override fun inject(component: LibraryComponent) {
        component.inject(viewModel)
    }
}