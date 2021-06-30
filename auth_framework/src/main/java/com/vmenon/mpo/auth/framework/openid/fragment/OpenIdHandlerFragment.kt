package com.vmenon.mpo.auth.framework.openid.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vmenon.mpo.auth.framework.di.dagger.AuthComponentProvider
import com.vmenon.mpo.auth.framework.openid.di.dagger.DaggerOpenIdAuthComponent
import com.vmenon.mpo.auth.framework.openid.di.dagger.OpenIdAuthComponent
import com.vmenon.mpo.auth.framework.openid.viewmodel.OpenIdHandlerViewModel
import com.vmenon.mpo.system.framework.di.dagger.SystemFrameworkComponentProvider

class OpenIdHandlerFragment : Fragment() {
    private val viewModel: OpenIdHandlerViewModel by lazy {
        ViewModelProvider(this)[OpenIdHandlerViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(setupComponent(context.applicationContext))
    }

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

    private fun setupComponent(context: Context): OpenIdAuthComponent {
        val systemFrameworkComponent =
            (context as SystemFrameworkComponentProvider).systemFrameworkComponent()
        val authComponent = (context as AuthComponentProvider).authComponent()
        return DaggerOpenIdAuthComponent.builder()
            .authComponent(authComponent)
            .systemFrameworkComponent(systemFrameworkComponent)
            .build()
    }

    private fun inject(component: OpenIdAuthComponent) {
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