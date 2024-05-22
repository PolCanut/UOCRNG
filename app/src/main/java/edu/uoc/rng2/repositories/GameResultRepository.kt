package edu.uoc.rng2.repositories

import edu.uoc.rng2.Constants
import edu.uoc.rng2.db.AppDatabase
import edu.uoc.rng2.db.dao.GameResultDao
import edu.uoc.rng2.models.GameResult
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

// Repositorio para gestionar los resultados del juego.
class GameResultRepository @Inject constructor(
    private val gameResultDao: GameResultDao,
) {
    // Obtiene un GameResult por su ID.
    // @param id ID del GameResult a obtener.
    // @return Un objeto Single que emite el GameResult correspondiente.
    fun getGameResultById(id: Long) = gameResultDao
        .getGameResult(id)
        .subscribeOn(Schedulers.io())

    // Obtiene un flujo de todos los resultados del juego.
    // @return Un objeto Flowable que emite una lista de GameResult.
    val history = gameResultDao
        .loadResults()
        .subscribeOn(Schedulers.io())

    // Obtiene el saldo actual del usuario.
    // Si no hay resultados, retorna el saldo inicial definido en Constants.
    // @return Un objeto Flowable que emite el saldo actual del usuario.
    fun currentBalance(): Flowable<Int> {
        return gameResultDao
            .lastResultFlowable()
            .subscribeOn(Schedulers.io())
            .map { gameResults ->
                if (gameResults.isEmpty()) {
                    Constants.INITIAL_BALANCE
                } else {
                    val first = gameResults.first()
                    first.balance
                }
            }
    }

    // Inserta un nuevo resultado del juego en la base de datos y devuelve su ID.
    // @param gameResult El resultado del juego a insertar en la base de datos.
    // @return El ID del nuevo GameResult insertado.
    fun insertGameResult(gameResult: GameResult): Long {
        return gameResultDao.insert(gameResult)
    }
}