package com.vmenon.mpo.common.framework.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.vmenon.mpo.common.domain.ContentEvent

fun <T> LiveData<ContentEvent<T>>.observeUnhandled(
    lifecycleOwner: LifecycleOwner,
    action: (T) -> Unit
) {
    observe(lifecycleOwner) { event ->
        event.unhandledContent()?.let { content ->
            action(content)
        }
    }
}