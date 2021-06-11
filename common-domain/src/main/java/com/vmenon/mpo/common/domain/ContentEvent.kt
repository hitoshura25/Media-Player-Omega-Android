package com.vmenon.mpo.common.domain

// For example, can help to address re-emission of a saved LiveData value
class ContentEvent<T>(
    private val content: T,
    private var handled: Boolean = false
) {
    fun unhandledContent(): T? {
        return if (handled) {
            null
        } else {
            handled = true
            content
        }
    }

    fun anyContent(): T = content
}

fun <T> T.toContentEvent() = ContentEvent(this)