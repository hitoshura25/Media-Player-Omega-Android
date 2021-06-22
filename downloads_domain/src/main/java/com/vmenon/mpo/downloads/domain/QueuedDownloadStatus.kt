package com.vmenon.mpo.downloads.domain

enum class QueuedDownloadStatus {
    PENDING,
    RUNNING,
    PAUSED,
    FAILED,
    SUCCESSFUL,
    NOT_QUEUED
}