package com.sdu.threads.presentation.home

import android.content.Context
import android.net.Uri
import com.sdu.threads.domain.model.UploadFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun toUploadFiles(context: Context, uris: List<Uri>): List<UploadFile> {
    return withContext(Dispatchers.IO) {
        uris.mapNotNull { uri ->
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@mapNotNull null
            val name = uri.lastPathSegment ?: "file"
            val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
            UploadFile(name = name, bytes = bytes, mimeType = mime)
        }
    }
}
