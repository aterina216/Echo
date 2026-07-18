package com.example.echo

import android.app.Application
import com.example.echo.di.AppComponent
import com.example.echo.di.DaggerAppComponent

class EchoApplication: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)   // ← теперь передаём Application
            .build()
    }
}