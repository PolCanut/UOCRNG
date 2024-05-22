package edu.uoc.rng2

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.uoc.rng2.repositories.MusicLibraryRepository
import edu.uoc.rng2.repositories.UriRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicLibraryRepository: MusicLibraryRepository,
    private val uriRepository: UriRepository,
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val currentSong = musicLibraryRepository.currentSong

    fun setCustomSong(uri: Uri) {
        val inputStream = uriRepository.getInputStreamFromUri(uri)

        if (inputStream != null) {
            val disposable = musicLibraryRepository.setCustomSong(inputStream)
                .doFinally { inputStream.close() }
                .doOnError { Log.e(TAG, "Error setting custom song ${it.message}", it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

            compositeDisposable.add(disposable)
        }
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }
}