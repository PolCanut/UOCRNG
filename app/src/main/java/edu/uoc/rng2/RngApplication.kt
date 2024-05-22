package edu.uoc.rng2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import edu.uoc.rng2.Notifications.CHANNEL_GAME_RESULT
import edu.uoc.rng2.db.AppDatabase
import javax.inject.Inject


@HiltAndroidApp
class RngApplication : Application() {
    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        createChannels()
    }

    private fun createChannels() {
        val channel = NotificationChannel(
            CHANNEL_GAME_RESULT,
            getString(R.string.game_result),
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        notificationManager.createNotificationChannel(channel)
    }
}