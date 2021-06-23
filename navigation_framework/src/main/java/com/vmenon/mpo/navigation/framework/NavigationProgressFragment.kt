package com.vmenon.mpo.navigation.framework

import androidx.navigation.dynamicfeatures.fragment.ui.AbstractProgressFragment

class NavigationProgressFragment : AbstractProgressFragment() {
    override fun onCancelled() {
        TODO("Not yet implemented")
    }

    override fun onFailed(errorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onProgress(status: Int, bytesDownloaded: Long, bytesTotal: Long) {
        TODO("Not yet implemented")
    }
}