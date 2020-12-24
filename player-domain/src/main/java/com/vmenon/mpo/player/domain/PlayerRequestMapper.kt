package com.vmenon.mpo.player.domain

interface PlayerRequestMapper<T> {
    fun createMediaId(item: T): String
}