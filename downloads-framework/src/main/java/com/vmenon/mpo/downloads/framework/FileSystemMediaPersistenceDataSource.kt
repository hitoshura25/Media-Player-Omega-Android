package com.vmenon.mpo.downloads.framework

import android.app.DownloadManager
import android.content.Context
import android.webkit.URLUtil
import com.vmenon.mpo.downloads.data.MediaPersistenceDataSource
import com.vmenon.mpo.downloads.domain.DownloadModel
import com.vmenon.mpo.extensions.writeToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileSystemMediaPersistenceDataSource(context: Context) : MediaPersistenceDataSource {
    private val downloadManager: DownloadManager = context.getSystemService(
        Context.DOWNLOAD_SERVICE
    ) as DownloadManager

    private val filesDir = context.filesDir

    override suspend fun storeMediaAndGetPath(download: DownloadModel): String =
        withContext(Dispatchers.IO) {
            val filename = URLUtil.guessFileName(
                download.episode.downloadUrl,
                null,
                null
            )
            val showDir = File(filesDir, download.episode.show.name)
            showDir.mkdir()
            val episodeFile = File(showDir, filename)
            downloadManager.openDownloadedFile(download.downloadManagerId).writeToFile(episodeFile)
            episodeFile.path
        }
}