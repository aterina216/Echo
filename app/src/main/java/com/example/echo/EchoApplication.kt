package com.example.echo

import android.app.Application
import android.util.Log
import com.example.echo.di.AppComponent
import com.example.echo.di.DaggerAppComponent
import com.example.echo.service.EchoService
import com.example.echo.util.createNotificationChannel

class EchoApplication: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("EchoApp", "Application onCreate started")
        Log.d("EchoApp", "createNotificationChannel вызван")
        createNotificationChannel(this, EchoService.CHANNEL_ID, "Echo Playback")
        appComponent = DaggerAppComponent.builder()
            .application(this)   // ← теперь передаём Application
            .build()
        Log.d("EchoApp", "Application onCreate finished")
    }
}