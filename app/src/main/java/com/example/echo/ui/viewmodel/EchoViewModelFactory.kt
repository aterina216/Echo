package com.example.echo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.player.ExoPlayerMusicPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.domain.player.MusicPlayer
import com.example.echo.util.SleepTimer
import kotlin.concurrent.timer

class EchoViewModelFactory(
    private val player: EchoPlayer,
    private val visualizer: AudioVisualizer,
    private val repository: RadioRepository,
    private val settingsRepository: SettingsRepository,
    val timer: SleepTimer
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EchoViewModel(
            player = player,
            visualizer = visualizer,
            repository = repository,
            settingsRepository,
            timer
        ) as T
    }
}