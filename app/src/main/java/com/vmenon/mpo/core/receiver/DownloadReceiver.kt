package com.vmenon.mpo.core.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vmenon.mpo.core.work.DownloadCompleteWorker

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1).let { downloadId ->
                Log.d("DownloadReceiver", "Download id $downloadId")
                if (downloadId != -1L) {
                    WorkManager.getInstance(context).enqueue(
                        OneTimeWorkRequestBuilder<DownloadCompleteWorker>()
                            .setInputData(
                                Data.Builder()
                                    .putLong(DownloadCompleteWorker.INPUT_DOWNLOAD_ID, downloadId)
                                    .build()
                            ).build()
                    )
                }
            }
        }
    }
}