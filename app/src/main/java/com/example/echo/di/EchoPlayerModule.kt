package com.example.echo.di

import android.app.Application
import android.content.Context
import android.media.AudioAttributes as AndroidAudioAttributes
import android.media.AudioManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.example.echo.data.player.AudioVisualizer
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
    fun provideExoPlayer(context: Context): ExoPlayer {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setAllowedCapturePolicy(AndroidAudioAttributes.ALLOW_CAPTURE_BY_ALL)
        }

        // Используем AudioAttributes, которые отключают audio offload.
        // USAGE_VOICE_COMMUNICATION и CONTENT_TYPE_SPEECH заставляют систему
        // направлять звук через software-путь, доступный для Visualizer.
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_VOICE_COMMUNICATION)   // ← ключевое: не медиа, а голос
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)     // ← речь вместо музыки
            .build()

        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
    }
    @Provides
    @Singleton
    fun provideEchoPlayer(player: ExoPlayer): EchoPlayer =
        EchoPlayer(player)

    @Provides
    @Singleton
    fun provideAudioVisualizer(exoPlayer: ExoPlayer): AudioVisualizer =
        AudioVisualizer(exoPlayer)
}