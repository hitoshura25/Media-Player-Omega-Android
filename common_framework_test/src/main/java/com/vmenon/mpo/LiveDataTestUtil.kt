package com.vmenon.mpo

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.vmenon.mpo.common.domain.ContentEvent
import org.junit.Assert.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun <T> LiveData<T>.test(): LiveDataTestObserver<T> = LiveDataTestObserver<T>().also {
    observeForever(it)
}

fun <T> LiveData<T>.noValueExpected(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
) {
    try {
        getOrAwaitValue(time, timeUnit)
        throw IllegalStateException("No value should be returned")
    } catch (exception: TimeoutException) {
        // expected
    }
}

class LiveDataTestObserver<T> : Observer<T> {
    private val values = mutableListOf<T>()
    val capturedValues: List<T> = values

    override fun onChanged(t: T) {
        values.add(t)
    }

    fun assertValues(vararg assertions: (T) -> Unit) {
        assertEquals("Observed values size does not match assertions", assertions.size, values.size)
        values.forEachIndexed { index, t -> assertions[index](t) }
    }

    fun assertValues(vararg expectedValues: T) {
        assertEquals(expectedValues.toList(), values)
    }
}

fun <T> LiveDataTestObserver<ContentEvent<T>>.assertValues(vararg expectedValues: T) {
    capturedValues.forEachIndexed { index, contentEvent ->
        assertEquals(expectedValues[index], contentEvent.anyContent())
    }
}