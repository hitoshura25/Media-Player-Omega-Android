package com.vmenon.mpo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmenon.mpo.common.domain.System
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import javax.inject.Inject

abstract class BaseFragment<COMPONENT : Any> : Fragment() {
    lateinit var component: COMPONENT

    @Inject
    protected lateinit var navigationController: NavigationController

    @Inject
    protected lateinit var system: System

    override fun onDestroyView() {
        super.onDestroyView()
        system.println("${javaClass.name} onDestroyView")
    }

    override fun onStop() {
        super.onStop()
        system.println("${javaClass.name} onStop")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = setupComponent(context.applicationContext)
        inject(component)
        system.println("${javaClass.name} onAttach")
    }

    override fun onPause() {
        super.onPause()
        system.println("${javaClass.name} onPause")

    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        system.println("${javaClass.name} onActivityCreated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        system.println("${javaClass.name} onCreate")
    }

    override fun onStart() {
        super.onStart()
        system.println("${javaClass.name} onStart")
    }

    override fun onResume() {
        super.onResume()
        system.println("${javaClass.name} onResume")
        if (this is NavigationOrigin<*>) {
            navigationController.setOrigin(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        system.println("${javaClass.name} onDetach")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        system.println("${javaClass.name} onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        system.println("${javaClass.name} onViewCreated")
    }

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL : ViewModel> viewModel(): Lazy<VIEW_MODEL> = lazy {
        ViewModelProvider(this)[VIEW_MODEL::class.java]
    }
}