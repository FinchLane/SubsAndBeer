package com.example.barbershop.viewmodel.galery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun getMediaFiles(context: Context): List<Uri> {
    val mediaUris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.MIME_TYPE)
    val queryUri = MediaStore.Files.getContentUri("external")
    val selection = "${MediaStore.MediaColumns.MIME_TYPE}=? OR ${MediaStore.MediaColumns.MIME_TYPE}=?"
    val selectionArgs = arrayOf("image/jpeg", "video/mp4")

    context.contentResolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val uri = ContentUris.withAppendedId(queryUri, id)
            mediaUris.add(uri)
        }
    }
    return mediaUris
}