package com.vmenon.mpo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationOrigin
import javax.inject.Inject

abstract class BaseFragment<COMPONENT : Any> : Fragment() {
    lateinit var component: COMPONENT

    @Inject
    protected lateinit var navigationController: NavigationController
    override fun onDestroyView() {
        super.onDestroyView()
        println("${javaClass.name} onDestroyView")
    }

    override fun onStop() {
        super.onStop()
        println("${javaClass.name} onStop")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = setupComponent(context.applicationContext)
        inject(component)
        println("${javaClass.name} onAttach")
    }

    override fun onPause() {
        super.onPause()
        println("${javaClass.name} onPause")

    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("${javaClass.name} onActivityCreated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("${javaClass.name} onCreate")
    }

    override fun onStart() {
        super.onStart()
        println("${javaClass.name} onStart")

    }

    override fun onResume() {
        super.onResume()
        println("${javaClass.name} onResume")
        if (this is NavigationOrigin<*>) {
            navigationController.setOrigin(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        println("${javaClass.name} onDetach")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("${javaClass.name} onCreateView")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL : ViewModel> viewModel(): Lazy<VIEW_MODEL> = lazy {
        ViewModelProvider(this)[VIEW_MODEL::class.java]
    }
}