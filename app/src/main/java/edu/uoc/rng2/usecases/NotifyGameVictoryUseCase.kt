package edu.uoc.rng2.usecases

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.uoc.rng2.MainActivity
import edu.uoc.rng2.Notifications.CHANNEL_GAME_RESULT
import edu.uoc.rng2.Notifications.ID_GAME_VICTORY
import edu.uoc.rng2.R
import javax.inject.Inject

class NotifyGameVictoryUseCase @Inject constructor(
    private val notificationManager: NotificationManager,
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(gameId: Long, turns: Int) {
        val notification = Notification.Builder(context, CHANNEL_GAME_RESULT)
            .setContentTitle(context.getString(R.string.victory))
            .setContentText(context.getString(R.string.win_in, turns))
            .setSmallIcon(R.drawable.ic_cup)
            .build()

        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.putExtra(MainActivity.GAME_ID_EXTRA, gameId)

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        notification.contentIntent = resultPendingIntent


        notificationManager.notify(ID_GAME_VICTORY, notification)
    }
}
