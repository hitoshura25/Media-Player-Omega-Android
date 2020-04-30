package com.vmenon.mpo.view.activity

import androidx.appcompat.app.AppCompatActivity

import com.vmenon.mpo.viewmodel.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var subscriptions: CompositeDisposable

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    override fun onStart() {
        super.onStart()
        subscriptions = CompositeDisposable()
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }
}
