package com.example.echo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.player.ExoPlayerMusicPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.domain.player.MusicPlayer

class EchoViewModelFactory(
    private val player: EchoPlayer,
    private val visualizer: AudioVisualizer,
    private val repository: RadioRepository,
    private val settingsRepository: SettingsRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EchoViewModel(
            player = player,
            visualizer = visualizer,
            repository = repository,
            settingsRepository
        ) as T
    }
}