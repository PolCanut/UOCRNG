package edu.uoc.rng2.ui.screens.sync

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.uoc.rng2.usecases.SyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    application: Application,
    private val syncUseCase: SyncUseCase
) : AndroidViewModel(application) {

    fun syncData() {
        viewModelScope.launch {
            syncUseCase.execute()
        }
    }
}
