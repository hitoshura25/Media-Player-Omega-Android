package com.vmenon.mpo.navigation.domain.player

import java.io.Serializable

sealed class MediaSource : Serializable
data class FileMediaSource(val mediaFile: String) : MediaSource()
