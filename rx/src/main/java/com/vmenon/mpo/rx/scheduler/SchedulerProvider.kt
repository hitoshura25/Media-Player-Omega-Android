package com.vmenon.mpo.rx.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun io(): Scheduler
    fun main(): Scheduler
    fun computation(): Scheduler
}