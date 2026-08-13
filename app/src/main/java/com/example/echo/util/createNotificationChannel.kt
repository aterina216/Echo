package com.example.echo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

fun createNotificationChannel(context: Context, channelId: String, name: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.d("EchoApp", "Создаю канал $channelId")
        val channel = NotificationChannel(
            channelId,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d("EchoApp", "Channel $channelId created")
    }
    else{
        Log.d("EchoApp", "API level < 26, channel not created")
    }
}