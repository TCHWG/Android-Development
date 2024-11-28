package com.kuroakevizago.aira.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.IOException

fun getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val tempFile = File(context.cacheDir, "temp_file")

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Compress the file to ensure it's no larger than 1 MB
        return compressFileTo1MB(context, tempFile)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun compressFileTo1MB(context: Context, file: File): File? {
    try {
        // Check if the file is an image
        val bitmap = BitmapFactory.decodeFile(file.path)
        if (bitmap != null) {
            var quality = 100
            val compressedFile = File(context.cacheDir, "compressed_temp_file.jpg")

            // Compress the image iteratively until its size is under 1 MB
            do {
                compressedFile.outputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }
                quality -= 5 // Reduce quality in increments of 5
            } while (compressedFile.length() > 1_000_000 && quality > 5)

            // Return the compressed file
            return compressedFile
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun createCustomTempFile(context: Context): File {
    val fileName = "temp_photo_${System.currentTimeMillis()}.jpg"
    val tempFile = File(context.cacheDir, fileName)

    try {
        // Create a new file if it doesn't exist
        if (!tempFile.exists()) {
            tempFile.createNewFile()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return tempFile
}