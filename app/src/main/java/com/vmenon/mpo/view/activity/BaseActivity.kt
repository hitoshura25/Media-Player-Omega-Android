package com.vmenon.mpo.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.vmenon.mpo.di.AppComponent
import com.vmenon.mpo.MPOApplication
import com.vmenon.mpo.viewmodel.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var subscriptions: CompositeDisposable

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    private lateinit var appComponent: AppComponent

    protected abstract fun inject(appComponent: AppComponent)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = (application as MPOApplication).appComponent
        inject(appComponent)
    }

    override fun onStart() {
        super.onStart()
        subscriptions = CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }
}
