package edu.uoc.rng2.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.uoc.rng2.db.AppDatabase
import edu.uoc.rng2.db.RngDatabase
import edu.uoc.rng2.db.dao.GameResultDao

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    fun provideRngDatabase(@ApplicationContext context: Context): RngDatabase {
        return Room
            .databaseBuilder(context, RngDatabase::class.java, "app.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideGameResultDao(db: RngDatabase): GameResultDao = db.gameResultDao()
}