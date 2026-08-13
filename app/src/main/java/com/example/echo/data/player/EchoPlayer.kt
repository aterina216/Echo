package com.example.echo.data.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.echo.domain.player.MusicPlayer

class EchoPlayer(private val exoPlayer: ExoPlayer): MusicPlayer {

    override fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun isPlaying(): Boolean = exoPlayer.isPlaying

    override fun release() {
        exoPlayer.release()
    }
}