package com.vmenon.mpo.core

import com.vmenon.mpo.api.Episode
import com.vmenon.mpo.api.Show

import org.parceler.Parcel

@Parcel
class Download {
    var show: Show? = null
        internal set
    var episode: Episode? = null
        internal set
    @get:Synchronized
    @set:Synchronized
    var total = 0
    @get:Synchronized
    var progress = 0
        internal set

    constructor()

    constructor(show: Show, episode: Episode) {
        this.show = show
        this.episode = episode
    }

    @Synchronized
    fun addProgress(progress: Int) {
        this.progress += progress
    }
}
