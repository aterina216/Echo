package com.example.echo.data.player

import androidx.media3.exoplayer.ExoPlayer
import com.example.echo.domain.player.MusicPlayer

class ExoPlayerMusicPlayer(private val exoPlayer: ExoPlayer): MusicPlayer {
    override fun play(url: String) {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun release() {
        exoPlayer.release()
    }

}