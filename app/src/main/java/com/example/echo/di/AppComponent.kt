package com.example.echo.di

import android.app.Application
import android.content.Context
import com.example.echo.EchoApplication
import com.example.echo.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [EchoPlayerModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder   // ← Application с @BindsInstance
        fun build(): AppComponent
    }

    fun inject(app: EchoApplication)
    fun inject(activity: MainActivity)
}