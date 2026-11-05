package com.example.echo.di

import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.player.ExoPlayerMusicPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.domain.player.MusicPlayer
import com.example.echo.ui.viewmodel.EchoViewModelFactory
import com.example.echo.util.SleepTimer
import dagger.Module
import dagger.Provides

@Module
class ViewModuleFactoryModule {

    @Provides
    fun provideViewModelFactory(
        player: EchoPlayer,
        visualizer: AudioVisualizer,
        repository: RadioRepository,
        settingsRepository: SettingsRepository,
        timer: SleepTimer
    ): EchoViewModelFactory {
        return EchoViewModelFactory(player, visualizer, repository, settingsRepository, timer)
    }

    @Provides
    fun provideSleepTimer() = SleepTimer()
}