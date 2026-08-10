package com.example.echo.di

import android.app.Application
import android.content.Context
import com.example.echo.EchoApplication
import com.example.echo.MainActivity
import com.example.echo.service.EchoService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [EchoPlayerModule::class, RepositoryModule::class, ViewModuleFactoryModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder   // ← Application с @BindsInstance
        fun build(): AppComponent
    }

    fun inject(app: EchoApplication)
    fun inject(activity: MainActivity)
    fun inject(service: EchoService)
}