package edu.uoc.rng2.ui.screens.game

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.uoc.rng2.usecases.NotifyGameVictoryUseCase
import edu.uoc.rng2.usecases.SaveGameResultUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// Número inicial del juego.
private const val INITIAL_GAME_NUMBER = 100

// Número mínimo del juego.
private const val MIN_GAME_NUMBER = 1

// Retraso para la jugada de la CPU en milisegundos.
private const val CPU_PLAY_DELAY = 1000L

// ViewModel para la pantalla del juego.
@HiltViewModel
class GameViewModel @Inject constructor(
    private val notifyGameVictoryUseCase: NotifyGameVictoryUseCase,
    private val locationManager: LocationManager,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _isSavingGame = MutableStateFlow(false)
    val isSavingGame = _isSavingGame.asStateFlow()

    // Manejador de suscripciones.
    private val compositeDisposable = CompositeDisposable()

    // Fecha actual en milisegundos desde el inicio de la época.
    private val date = System.currentTimeMillis()

    // Estado del jugador actual (persona o CPU).
    val currentPlayer = MutableStateFlow(Player.PERSON)

    // Número actual en el juego.
    val currentNumber = MutableStateFlow(INITIAL_GAME_NUMBER)

    // Flujo compartido para indicar que la partida ha terminado.
    val gameEnded = MutableSharedFlow<Long>()

    // Número de turnos realizados por la persona.
    private var personTurns = 0

    // Limpia las suscripciones cuando se destruye el ViewModel.
    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    // Genera un nuevo número en el juego.
    fun generateNewNumber() {
        personTurns++

        val newNumber = newRandomNumber()

        if (isGameFinished(newNumber)) {
            finishGame()
        } else {
            currentNumber.value = newNumber
            currentPlayer.value = Player.CPU

            cpuTurn()
        }
    }

    private fun finishGame() {
        _isSavingGame.value = true

        val disposable = persistGameResult()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewModelScope.launch {
                    gameEnded.emit(it)

                    if (userWon()) {
                        notifyGameVictoryUseCase(it, personTurns)
                    }
                }
            }

        compositeDisposable.add(disposable)
    }

    // Jugada de la CPU.
    private fun cpuTurn() = viewModelScope.launch {
        delay(CPU_PLAY_DELAY)

        val newNumber = newRandomNumber()

        if (isGameFinished(newNumber)) {
            finishGame()
        } else {
            currentNumber.value = newNumber
            currentPlayer.value = Player.PERSON
        }
    }

    // Guarda el resultado del juego.
    private fun persistGameResult(): Maybe<Long> {

        return Maybe.create<LocationWrapper> { emitter ->
            val isPermissionGranted =
                context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (locationManager.isLocationEnabled && isPermissionGranted) {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.mainExecutor,
                ) { location: Location? ->
                    if (location != null) {
                        emitter.onSuccess(LocationWrapper(location))
                    }
                }
            } else {
                emitter.onSuccess(LocationWrapper(null))
            }
        }.flatMap { locationWrapper ->
            val location = locationWrapper.location

            saveGameResultUseCase(
                personTurns,
                date,
                userWon(),
                latitude = location?.latitude,
                longitude = location?.longitude,
            )
        }
    }

    private fun userWon(): Boolean = currentPlayer.value != Player.PERSON

    // Comprueba si el juego ha terminado.
    private fun isGameFinished(newNumber: Int): Boolean {
        return newNumber == MIN_GAME_NUMBER
    }

    // Genera un nuevo número aleatorio.
    private fun newRandomNumber(): Int {
        val number = currentNumber.value

        return Random.nextInt(MIN_GAME_NUMBER, number)
    }
}

class LocationWrapper(val location: Location?)

// Enumeración para representar el tipo de jugador.
enum class Player {
    PERSON,
    CPU
}