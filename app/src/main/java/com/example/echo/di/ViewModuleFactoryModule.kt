package com.example.echo.di

import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.player.ExoPlayerMusicPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.domain.player.MusicPlayer
import com.example.echo.ui.viewmodel.EchoViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModuleFactoryModule {

    @Provides
    fun provideViewModelFactory(
        player: EchoPlayer,
        visualizer: AudioVisualizer,
        repository: RadioRepository
    ): EchoViewModelFactory {
        return EchoViewModelFactory(player, visualizer, repository)
    }
}