package com.vmenon.mpo.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import com.vmenon.mpo.system.domain.Logger
import com.vmenon.mpo.view.activity.BaseActivity
import javax.inject.Inject

abstract class BaseFragment<COMPONENT : Any> : Fragment() {
    lateinit var component: COMPONENT

    @Inject
    protected lateinit var navigationController: NavigationController

    @Inject
    protected lateinit var logger: Logger

    override fun onDestroyView() {
        super.onDestroyView()
        logger.println("${javaClass.name} onDestroyView")
    }

    override fun onStop() {
        super.onStop()
        logger.println("${javaClass.name} onStop")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = setupComponent(context.applicationContext)
        inject(component)
        logger.println("${javaClass.name} onAttach")
    }

    override fun onPause() {
        super.onPause()
        logger.println("${javaClass.name} onPause")

    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logger.println("${javaClass.name} onActivityCreated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.println("${javaClass.name} onCreate")
    }

    override fun onStart() {
        super.onStart()
        logger.println("${javaClass.name} onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.println("${javaClass.name} onResume")
        if (this is NavigationOrigin<*>) {
            navigationController.setOrigin(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        logger.println("${javaClass.name} onDetach")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.println("${javaClass.name} onViewCreated")
    }

    protected fun drawerLayout(): DrawerLayout? =
        (requireActivity() as? BaseActivity<*>)?.drawerLayout()

    protected fun navigationView(): NavigationView? =
        (requireActivity() as? BaseActivity<*>)?.navigationView()

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL : ViewModel> viewModel(): Lazy<VIEW_MODEL> = lazy {
        ViewModelProvider(this)[VIEW_MODEL::class.java]
    }
}