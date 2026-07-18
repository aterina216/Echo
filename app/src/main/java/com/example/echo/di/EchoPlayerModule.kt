package com.example.echo.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.echo.data.player.EchoPlayer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object EchoPlayerModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    fun provideExoPlayer(context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun provideEchoPlayer(player: ExoPlayer): EchoPlayer =
        EchoPlayer(player)
}