package com.example.echo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echo.data.player.AudioVisualData
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.player.ExoPlayerMusicPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.domain.model.RadioStation
import com.example.echo.domain.player.MusicPlayer
import com.example.echo.util.SleepTimer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EchoViewModel(
    private val player: EchoPlayer,
    private val visualizer: AudioVisualizer,
    private val repository: RadioRepository
): ViewModel() {

    val stations: List<RadioStation> = repository.getAllStations()
    val currentStation: StateFlow<RadioStation> = repository.currentStation

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _visualData = MutableStateFlow<AudioVisualData?>(null)
    val visualData: StateFlow<AudioVisualData?> = _visualData

    val sleepTimer = SleepTimer()
    val remainings: StateFlow<Long> = sleepTimer.remainings

    private val _requestPermission = MutableSharedFlow<Unit>()
    val requestPermission: SharedFlow<Unit> = _requestPermission

    fun selectStation(station: RadioStation){
        repository.setCurrentStation(station)
        if (_isPlaying.value) {
            play()
        }
    }

    fun togglePlayPause(){
        if(_isPlaying.value) {
            pause()
        }
        else {
            play()
        }
    }

    fun startTimer(minutes: Int) {
        sleepTimer.start(minutes) {pause()}
    }

    fun cancelTimer() {
        sleepTimer.cancel()
    }

    private fun play() {
        player.play(currentStation.value.url)
        _isPlaying.value = true
        viewModelScope.launch {
            visualizer.start().collect { data ->
                _visualData.value = data
            }
        }
    }

    private fun pause() {
        player.pause()
        visualizer.stop()
        _isPlaying.value = false
        _visualData.value = null
        sleepTimer.cancel()
    }

    fun onPermissionGranted() {
        play()
    }

    fun requestPermissionIfNeeded(){
        viewModelScope.launch { _requestPermission.emit(Unit) }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        visualizer.stop()
    }
}