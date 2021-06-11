package com.vmenon.mpo.login.framework.openid.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponent
import com.vmenon.mpo.login.framework.di.LoginFrameworkComponentProvider
import com.vmenon.mpo.login.framework.openid.viewmodel.OpenIdHandlerViewModel
import com.vmenon.mpo.view.BaseFragment

class OpenIdHandlerFragment : BaseFragment<LoginFrameworkComponent>() {
    private val viewModel: OpenIdHandlerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreated(this)
        viewModel.authenticated().observe(this) {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    override fun setupComponent(context: Context): LoginFrameworkComponent =
        (context as LoginFrameworkComponentProvider).loginFrameworkComponent()

    override fun inject(component: LoginFrameworkComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    companion object {
        fun forAuthentication(): OpenIdHandlerFragment =
            OpenIdHandlerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_OPERATION, Operation.PERFORM_AUTH)
                }
            }

        fun forLogOut(): OpenIdHandlerFragment =
            OpenIdHandlerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_OPERATION, Operation.LOGOUT)
                }
            }

        enum class Operation {
            PERFORM_AUTH,
            LOGOUT
        }

        const val EXTRA_OPERATION = "auth_operation"
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(this)
    }
}