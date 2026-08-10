package com.example.echo.domain.player

interface MusicPlayer {

    fun play(url: String)
    fun pause()
    fun isPlaying(): Boolean
    fun release()
}