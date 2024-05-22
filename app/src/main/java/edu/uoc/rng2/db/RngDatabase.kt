// Paquete para manejar la base de datos y sus entidades
package edu.uoc.rng2.db

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.uoc.rng2.db.dao.GameResultDao
import edu.uoc.rng2.models.GameResult
import java.lang.IllegalStateException


// Clase de Room para gestionar los resultados del juego.
@Database(
    entities = [
        GameResult::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class RngDatabase : RoomDatabase() {
    // Método abstracto para obtener el GameResultDao.
    abstract fun gameResultDao(): GameResultDao
}

object AppDatabase {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE GameResult ADD COLUMN longitude REAL")
            db.execSQL("ALTER TABLE GameResult ADD COLUMN latitude REAL")
        }
    }
}