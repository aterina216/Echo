package com.example.echo.service

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.echo.util.notifyPermission
import com.example.echo.util.reminderNotification

class DailyReminderWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {

        return try {
            
            if (!notifyPermission(appContext)) {
                return Result.success()
            }

            val notification = reminderNotification(appContext, CHANNEL_ID)
            val manager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, notification)
            Result.success()
        }
        catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val CHANNEL_ID = "echo_reminders"
        const val NOTIFICATION_ID = 101
    }
}