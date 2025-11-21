package com.maverick.kmjshowroom.API

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

object MultipartUtil {

    fun createPart(value: String): RequestBody =
        RequestBody.Companion.create("text/plain".toMediaTypeOrNull(), value)

    fun prepareFile(field: String, uri: Uri, context: Context): MultipartBody.Part? {
        val cr = context.contentResolver ?: return null

        val type = cr.getType(uri) ?: "image/*"
        var fileName = "image_${System.currentTimeMillis()}.jpg"

        cr.query(uri, null, null, null, null)?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) fileName = it.getString(index)
            }
        }

        val inputStream = cr.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(tempFile)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val requestBody = RequestBody.Companion.create(type.toMediaTypeOrNull(), tempFile)
        return MultipartBody.Part.createFormData(field, fileName, requestBody)
    }
}