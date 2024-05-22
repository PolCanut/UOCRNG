package edu.uoc.rng2.ui.screens.welcome

// Importaciones necesarias
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.uoc.rng2.PlaybackMediaController
import edu.uoc.rng2.repositories.GameResultRepository
import edu.uoc.rng2.repositories.MusicLibraryRepository
import javax.inject.Inject

// ViewModel para la pantalla de bienvenida
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    gameResultRepository: GameResultRepository,
    private val playbackMediaController: PlaybackMediaController,
    private val musicLibraryRepository: MusicLibraryRepository,
) : ViewModel() {
    val isAudioMuted = playbackMediaController.isMuted
    val currentSong = musicLibraryRepository.currentSong

    fun alternatePauseMusic() {
        playbackMediaController.alternatePause()
    }

    fun removeCustomSong() {
        musicLibraryRepository.removeCustomSong()
    }

    // LiveData que contiene el saldo actual del jugador
    val currentBalance = gameResultRepository.currentBalance() // Accede al saldo actual desde el repositorio
}