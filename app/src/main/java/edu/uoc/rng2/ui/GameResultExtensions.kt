package edu.uoc.rng2.ui

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import edu.uoc.rng2.R
import edu.uoc.rng2.models.GameResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val GameResult.icon: Int
    get() = when (this.userWon) {
        true -> R.drawable.ic_cup
        false -> R.drawable.ic_thumb_down
    }

val GameResult.iconColor: Color
    @Composable
    get() = when (this.userWon) {
        true -> MaterialTheme.colorScheme.primary
        false -> MaterialTheme.colorScheme.error
    }


val GameResult.title: String
    @Composable
    get() {
        val context = LocalContext.current

        return this.title(context)
    }

fun GameResult.title(context: Context): String = when (this.userWon) {
    true -> context.getString(R.string.win_in, this.turns)
    false -> context.getString(R.string.defeat_in, this.turns)
}

val GameResult.formattedDate: String
    get() {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yy - HH:mm:ss", Locale.ENGLISH)
        val date = Date(date)

        return simpleDateFormat.format(date)
    }