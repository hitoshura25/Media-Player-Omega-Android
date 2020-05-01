package com.vmenon.mpo.view.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmenon.mpo.navigation.NavigationController

import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var subscriptions: CompositeDisposable

    @Inject
    protected lateinit var navigationController: NavigationController

    override fun onStart() {
        super.onStart()
        subscriptions = CompositeDisposable()
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }

    protected inline fun <reified VIEW_MODEL: ViewModel> viewModel(): VIEW_MODEL {
        return ViewModelProvider(this)[VIEW_MODEL::class.java].apply {  }
    }
}
