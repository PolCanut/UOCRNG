package edu.uoc.rng2.repositories

import edu.uoc.rng2.db.SQLiteHelper
import edu.uoc.rng2.models.DataModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val dbHelper: SQLiteHelper
) {

    suspend fun syncData() {
        val sqliteData = dbHelper.getData()
        syncToFirebase(sqliteData)
        val firebaseData = syncFromFirebase()
        dbHelper.saveData(firebaseData)
    }

    private suspend fun syncToFirebase(data: List<DataModel>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("data")
        myRef.setValue(data).await()
    }

    private suspend fun syncFromFirebase(): List<DataModel> {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("data")
        val snapshot = myRef.get().await()
        return snapshot.children.mapNotNull {
            it.getValue(DataModel::class.java)
        }
    }
}
