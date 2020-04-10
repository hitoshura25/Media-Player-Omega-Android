package com.vmenon.mpo.core

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun io(): Scheduler
    fun main(): Scheduler
    fun computation(): Scheduler
}