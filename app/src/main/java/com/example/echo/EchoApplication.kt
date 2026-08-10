package com.example.echo

import android.app.Application
import com.example.echo.di.AppComponent
import com.example.echo.di.DaggerAppComponent
import com.example.echo.service.EchoService
import com.example.echo.util.createNotificationChannel

class EchoApplication: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, EchoService.CHANNEL_ID, "Echo Playback")
        appComponent = DaggerAppComponent.builder()
            .application(this)   // ← теперь передаём Application
            .build()
    }
}