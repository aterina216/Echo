package com.example.echo

import android.app.Application
import android.util.Log
import androidx.compose.material3.TimeInput
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.echo.di.AppComponent
import com.example.echo.di.DaggerAppComponent
import com.example.echo.service.DailyReminderWorker
import com.example.echo.service.EchoService
import com.example.echo.util.createNotificationChannel
import com.example.echo.util.createRemindersNotificationChannel
import java.util.concurrent.TimeUnit

class EchoApplication: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("EchoApp", "Application onCreate started")
        Log.d("EchoApp", "createNotificationChannel вызван")
        createNotificationChannel(this, EchoService.CHANNEL_ID, "Echo Playback")
        createRemindersNotificationChannel(this, DailyReminderWorker.CHANNEL_ID, "Daily Reminders")

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "echo_reminders",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        appComponent = DaggerAppComponent.builder()
            .application(this)   // ← теперь передаём Application
            .build()
        Log.d("EchoApp", "Application onCreate finished")
    }
}