package com.vmenon.mpo.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingActivity<COMPONENT : Any, BINDING : ViewBinding> :
    BaseActivity<COMPONENT>() {
    private var _binding: BINDING? = null
    protected val binding get() = _binding!!
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        _binding = bind(layoutInflater)
        setContentView(binding.root)
    }

    protected abstract fun bind(inflater: LayoutInflater): BINDING
}