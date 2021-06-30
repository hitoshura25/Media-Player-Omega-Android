package com.vmenon.mpo.view.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.system.domain.Logger

import javax.inject.Inject

abstract class BaseActivity<COMPONENT : Any> : AppCompatActivity() {
    lateinit var component: COMPONENT

    @Inject
    protected lateinit var navigationController: NavigationController

    @Inject
    protected lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = setupComponent(applicationContext)
        inject(component)
    }

    fun requireContentView() = getContentView()!!
    fun requireLoadingView() = getLoadingView()!!

    abstract fun getContentView(): View?
    abstract fun getLoadingView(): View?
    abstract fun drawerLayout(): DrawerLayout?
    abstract fun navigationView(): NavigationView?

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL : ViewModel> viewModel(): Lazy<VIEW_MODEL> = lazy {
        ViewModelProvider(this)[VIEW_MODEL::class.java]
    }
}
