package com.vmenon.mpo.login.framework.openid

import android.app.Activity
import com.vmenon.mpo.login.data.Authenticator
import com.vmenon.mpo.login.framework.openid.activity.OpenIdHandlerActivity

class OpenIdAuthenticator : Authenticator {
    override fun startAuthentication(context: Any) {
        if (context is Activity) {
            // Just launch the OpenIdHandlerActivity
            context.startActivity(OpenIdHandlerActivity.createPerformAuthIntent(context))
        } else {
            throw IllegalStateException("Context for ${javaClass.name} needs to be an Activity!")
        }
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}