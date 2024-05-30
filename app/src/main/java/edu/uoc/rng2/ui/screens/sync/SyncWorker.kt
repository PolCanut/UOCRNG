package edu.uoc.rng2.ui.screens.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.uoc.rng2.db.SQLiteHelper
import edu.uoc.rng2.models.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    workerParams: WorkerParameters,
    private val dbHelper: SQLiteHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun syncData() {
        val sqliteData = dbHelper.getData()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("data")

        myRef.setValue(sqliteData).await()

        val snapshot = myRef.get().await()
        val firebaseData = snapshot.children.mapNotNull { it.getValue(DataModel::class.java) }
        dbHelper.saveData(firebaseData)
    }
}
