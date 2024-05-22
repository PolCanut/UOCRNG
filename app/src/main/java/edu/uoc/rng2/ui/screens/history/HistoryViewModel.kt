package edu.uoc.rng2.ui.screens.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.uoc.rng2.repositories.GameResultRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val gameResultRepository: GameResultRepository,
) : ViewModel() {


    val history = gameResultRepository.history
}