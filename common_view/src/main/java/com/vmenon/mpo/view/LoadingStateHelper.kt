package com.vmenon.mpo.view

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View

class LoadingStateHelper internal constructor(
    private val loadingView: View,
    private val contentView: View?,
    private val useOverlay: Boolean
) {
    private val handler = Handler(Looper.getMainLooper())

    private val showLoadingStateRunnable = Runnable {
        toggleLoadingState()
    }

    private val showContentStateRunnable = Runnable {
        toggleContentState()
    }

    private var timeLoadingStarted: Long = -1L

    fun showLoadingState() {
        handler.postDelayed(showLoadingStateRunnable, MIN_DELAY_TO_SHOW_LOADING_MS)
    }

    fun showContentState() {
        if (hasLoadingStateBeenShowLongEnough()) {
            handler.removeCallbacks(showLoadingStateRunnable)
            toggleContentState()
        } else {
            handler.postDelayed(showContentStateRunnable, getShowContentDelayMs())
        }
    }

    fun reset() {
        handler.removeCallbacks(showContentStateRunnable)
        handler.removeCallbacks(showLoadingStateRunnable)
        timeLoadingStarted = -1L
    }

    private fun toggleLoadingState() {
        loadingView.visibility = View.VISIBLE

        if (!useOverlay) {
            contentView?.visibility = View.GONE
        }
        timeLoadingStarted = SystemClock.elapsedRealtime()
    }

    private fun toggleContentState() {
        loadingView.visibility = View.GONE
        contentView?.visibility = View.VISIBLE
    }

    private fun hasLoadingStateBeenShowLongEnough(): Boolean = timeLoadingStarted == -1L ||
            (SystemClock.elapsedRealtime() - timeLoadingStarted) > MIN_SHOW_LOADING_MS

    private fun getShowContentDelayMs(): Long =
        MIN_DELAY_TO_SHOW_LOADING_MS - (SystemClock.elapsedRealtime() - timeLoadingStarted)

    companion object {
        fun switchWithContent(contentView: View, loadingView: View) = LoadingStateHelper(
            contentView = contentView,
            loadingView = loadingView,
            useOverlay = false
        )

        fun overlayContent(loadingView: View) = LoadingStateHelper(
            loadingView = loadingView,
            contentView = null,
            useOverlay = true
        )

        private const val MIN_DELAY_TO_SHOW_LOADING_MS = 500L
        private const val MIN_SHOW_LOADING_MS = 500L
    }
}