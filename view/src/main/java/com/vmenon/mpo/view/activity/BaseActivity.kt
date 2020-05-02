package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmenon.mpo.navigation.NavigationController

import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity<COMPONENT: Any> : AppCompatActivity() {
    protected lateinit var subscriptions: CompositeDisposable

    lateinit var component: COMPONENT

    @Inject
    protected lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = setupComponent(applicationContext)
        inject(component)
    }

    override fun onStart() {
        super.onStart()
        subscriptions = CompositeDisposable()
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL: ViewModel> viewModel(): VIEW_MODEL {
        return ViewModelProvider(this)[VIEW_MODEL::class.java].apply {  }
    }
}
