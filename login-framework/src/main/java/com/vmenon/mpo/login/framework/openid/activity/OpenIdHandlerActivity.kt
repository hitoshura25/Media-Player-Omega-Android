package com.vmenon.mpo.login.framework.openid.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.vmenon.mpo.login.framework.databinding.ActivityOpenIdHandlerBinding
import com.vmenon.mpo.login.framework.di.AuthComponent
import com.vmenon.mpo.login.framework.di.AuthComponentProvider
import com.vmenon.mpo.login.framework.openid.viewmodel.OpenIdHandlerViewModel
import com.vmenon.mpo.view.activity.BaseViewBindingActivity

class OpenIdHandlerActivity :
    BaseViewBindingActivity<AuthComponent, ActivityOpenIdHandlerBinding>() {
    private val viewModel: OpenIdHandlerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreated(this)
        viewModel.authenticated().observe(this) { authenticated ->
            if (authenticated) {
                println("Authenticated!")
            } else {
                println("Not Authenticated!")
            }
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(this)
    }

    override fun onDestroy() {
        viewModel.onDestroyed()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleResult(requestCode, resultCode, data)
    }

    override fun setupComponent(context: Context): AuthComponent =
        (context as AuthComponentProvider).authComponent()

    override fun inject(component: AuthComponent) {
        component.inject(this)
        component.inject(viewModel)
    }

    override fun bind(inflater: LayoutInflater): ActivityOpenIdHandlerBinding =
        ActivityOpenIdHandlerBinding.inflate(inflater)

    companion object {
        fun createPerformAuthIntent(activity: Activity) =
            Intent(activity, OpenIdHandlerActivity::class.java).apply {
                putExtra(EXTRA_OPERATION, Operation.PERFORM_AUTH)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }

        fun createLogOutIntent(activity: Activity) =
            Intent(activity, OpenIdHandlerActivity::class.java).apply {
                putExtra(EXTRA_OPERATION, Operation.LOGOUT)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }

        enum class Operation {
            PERFORM_AUTH,
            LOGOUT
        }

        const val EXTRA_OPERATION = "auth_operation"
    }

    override fun getContentView(): View? = null

    override fun getLoadingView(): View? = null
}