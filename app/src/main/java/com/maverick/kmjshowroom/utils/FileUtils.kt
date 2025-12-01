package com.maverick.kmjshowroom.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveToDownload(context: Context, fileName: String, bytes: ByteArray): File? {
        val resolver = context.contentResolver

        val mimeType = when {
            fileName.endsWith(".pdf", true) -> "application/pdf"
            fileName.endsWith(".csv", true) -> "text/csv"
            else -> "application/octet-stream"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/KMJ Showroom")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        resolver.openOutputStream(uri)?.use { stream ->
            stream.write(bytes)
        }

        // finalize changes
        ContentValues().apply {
            put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, this, null, null)
        }

        return File("/storage/emulated/0/Download/KMJ Showroom/$fileName")
    }


    private fun getRealPath(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val index = it.getColumnIndex(MediaStore.MediaColumns.DATA)
            if (index >= 0 && it.moveToFirst()) it.getString(index) else null
        }
    }
}
