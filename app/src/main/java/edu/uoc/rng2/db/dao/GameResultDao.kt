// Paquete para manejar la base de datos y sus entidades
package edu.uoc.rng2.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.uoc.rng2.models.GameResult
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

// Interfaz DAO para la tabla GameResult.
@Dao
interface GameResultDao {
    // Inserta un nuevo GameResult en la base de datos.
    // Retorna el ID del nuevo GameResult.
    @Insert
    fun insert(gameResult: GameResult): Long

    // Obtiene un GameResult por su ID.
    // Retorna un solo GameResult envuelto en un objeto Single.
    @Query("SELECT * FROM GameResult WHERE id = :id")
    fun getGameResult(id: Long): Single<GameResult>

    // Carga todos los GameResult ordenados por ID de manera descendente.
    // Retorna una lista de GameResult envuelta en un objeto Flowable.
    @Query("SELECT * FROM GameResult ORDER BY id DESC")
    fun loadResults(): Flowable<List<GameResult>>

    // Obtiene el último GameResult insertado en la base de datos.
    // Retorna una lista que contiene el último GameResult envuelto en un objeto Flowable.
    @Query("SELECT * FROM GameResult ORDER BY id DESC LIMIT 1")
    fun lastResultFlowable(): Flowable<List<GameResult>>
}