package edu.uoc.rng2

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackMediaController @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val playState = MutableStateFlow(PlayState.TO_BE_INITIALIZED)

    val isMuted = playState.map {
        it == PlayState.PAUSED_BY_USER
    }

    private val player = ExoPlayer.Builder(context)
        .build()

    fun playMedia(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.stop()
        player.setMediaItem(mediaItem)
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        player.prepare()
        play()
    }

    fun alternatePause() {
        if (
            player.isPlaying &&
            playState.value == PlayState.PLAYING
        ) {
            player.pause()
            playState.value = PlayState.PAUSED_BY_USER
        } else {
            play()
        }
    }

    fun lifecyclePause() {
        if (
            player.isPlaying &&
            playState.value == PlayState.PLAYING
        ) {
            player.pause()
            playState.value = PlayState.PAUSED_BY_LIFECYCLE
        }
    }

    fun lifecyclePlay() {
        if (
            !player.isPlaying &&
            playState.value != PlayState.PAUSED_BY_USER
        ) {
            play()
        }
    }

    private fun play() {
        player.play()
        playState.value = PlayState.PLAYING
    }
}

enum class PlayState {
    TO_BE_INITIALIZED,
    PAUSED_BY_USER,
    PAUSED_BY_LIFECYCLE,
    PLAYING
}