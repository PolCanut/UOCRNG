package edu.uoc.rng2.repositories

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

class UriRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getInputStreamFromUri(uri: Uri): InputStream? {
        return context.contentResolver.openInputStream(uri)
    }
}