package com.vmenon.mpo.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<COMPONENT : Any> : Fragment() {
    lateinit var component: COMPONENT

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = setupComponent(context.applicationContext)
        inject(component)
    }

    protected abstract fun setupComponent(context: Context): COMPONENT
    protected abstract fun inject(component: COMPONENT)

    protected inline fun <reified VIEW_MODEL : ViewModel> viewModel(): Lazy<VIEW_MODEL> = lazy {
        ViewModelProvider(this)[VIEW_MODEL::class.java]
    }

}