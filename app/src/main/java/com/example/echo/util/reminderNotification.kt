package com.example.echo.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.echo.MainActivity

fun reminderNotification(context: Context, channelId: String): Notification {
    val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_media_play)
        .setContentTitle("Echo")
        .setContentText("Зайди и погрузись в умиротворяющий эмбиент или тягучий дроун")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
}