package com.vmenon.mpo.model

data class DownloadModel(
    val show: SubscribedShowModel,
    val episode: EpisodeModel,
    var total: Int = 0,
    var progress: Int = 0
) {
    @Synchronized
    fun addProgress(progress: Int) {
        this.progress += progress
    }
}
