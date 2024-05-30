package edu.uoc.rng2.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.uoc.rng2.models.DataModel

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL("CREATE TABLE data (id TEXT PRIMARY KEY, value TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS data")
        onCreate(db)
    }

    fun getData(): List<DataModel> {
        val dataList = mutableListOf<DataModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM data", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val value = cursor.getString(cursor.getColumnIndexOrThrow("value"))
                dataList.add(DataModel(id, value))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }

    fun saveData(data: List<DataModel>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            data.forEach {
                val values = ContentValues().apply {
                    put("id", it.id)
                    put("value", it.value)
                }
                db.insertWithOnConflict("data", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1
    }
}
