package com.example.echo.data.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class EchoPlayer(private val exoPlayer: ExoPlayer) {

    fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun release() {
        exoPlayer.release()
    }
}