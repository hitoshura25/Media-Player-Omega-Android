package com.vmenon.mpo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingFragment<COMPONENT : Any, BINDING : ViewBinding> :
    BaseFragment<COMPONENT>() {

    private var _binding: BINDING? = null
    protected val binding get() = _binding!!
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bind(inflater, container)
        return binding.root
    }

    protected abstract fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BINDING

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}