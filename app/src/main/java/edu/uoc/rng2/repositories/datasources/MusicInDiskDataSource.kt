package edu.uoc.rng2.repositories.datasources

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val CUSTOM_SONG_FILE_NAME = "custom_song.mp3"
private const val DEFAULT_SONG_NAME = "elevator.mp3"


interface IMusicInDiskDataSource {
    val currentSong: StateFlow<CurrentSong>
    fun setCustomSong(songInput: InputStream): Observable<Unit>
    fun removeCustomSong()
}

@Singleton
class MusicInDiskDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : IMusicInDiskDataSource {
    private val _currentSong = MutableStateFlow(loadCurrentSong())
    override val currentSong = _currentSong.asStateFlow()

    override fun setCustomSong(songInput: InputStream) = Observable.fromAction<Unit> {
        FileOutputStream(customSongFile).use { out ->
            songInput.copyTo(out)
        }

        refreshCurrentSong()
    }

    override fun removeCustomSong() {
        if (customSongFile.exists()) {
            customSongFile.delete()
        }

        refreshCurrentSong()
    }

    private fun refreshCurrentSong() {
        _currentSong.value = loadCurrentSong()
    }

    private fun loadCurrentSong(): CurrentSong {
        val customSong = getCustomMusic()

        return if (customSong != null) {
            CurrentSong(customSong, SongType.CUSTOM)
        } else {
            CurrentSong(getDefaultMusic(), SongType.DEFAULT)
        }
    }

    private fun getCustomMusic(): Uri? {
        val customSong = customSongFile

        return if (customSong.exists()) {
            Uri.fromFile(customSong)
        } else {
            null
        }
    }

    private fun getDefaultMusic(): Uri = Uri.parse("file:///android_asset/$DEFAULT_SONG_NAME")

    private val customSongFile: File
        get() = File(context.filesDir, CUSTOM_SONG_FILE_NAME)
}

enum class SongType {
    DEFAULT,
    CUSTOM
}

class CurrentSong(val uri: Uri, val songType: SongType)