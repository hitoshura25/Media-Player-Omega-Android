package com.vmenon.mpo.extensions

import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun ParcelFileDescriptor.writeToFile(file: File) {
    val inputStream = ParcelFileDescriptor.AutoCloseInputStream(this)
    val outputStream = FileOutputStream(file)
    var len: Int
    val buf = ByteArray(1024)
    try {
        while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
    } catch (e: IOException) {

    }

    outputStream.closeQuietly()
    inputStream.closeQuietly()
}

fun ParcelFileDescriptor.AutoCloseInputStream.closeQuietly() {
    try {
        close()
    } catch (e: IOException) {

    }
}

fun FileOutputStream.closeQuietly() {
    try {
        close()
    } catch (e: IOException) {

    }
}