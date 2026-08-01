package com.example.echo.data.player

import android.media.audiofx.Visualizer
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class AudioVisualizer(private val exoPlayer: ExoPlayer) {

    private var visualizer: Visualizer? = null
    private val _amplitude = MutableStateFlow(0f)
    val amplitudeFlow: Flow<Float> = _amplitude

    @OptIn(UnstableApi::class)
    fun start(): Flow<Float> {
        val sessionId = exoPlayer.audioSessionId
        if (sessionId == 0) {
            Log.w("AudioVisualizer", "Audio session not ready, will retry via listener")
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        val id = exoPlayer.audioSessionId
                        if (id != 0) setupVisualizer(id)
                    }
                }
            })
        } else {
            setupVisualizer(sessionId)
        }
        return amplitudeFlow
    }

    @OptIn(UnstableApi::class)
    private fun setupVisualizer(sessionId: Int) {
        try {
            visualizer?.release()
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            p0: Visualizer?,
                            p1: ByteArray?,
                            p2: Int
                        ) {
                            if (p1 != null) {
                                var sum = 0f
                                for (b in p1) {
                                    val amp = (b.toInt() and 0xFF) - 128
                                    sum += (amp *amp).toFloat()
                                }
                                val rms = Math.sqrt((sum/p1.size).toDouble()).toFloat()
                                val level = (rms / 128f).coerceIn(0f, 1f)
                               _amplitude.value = level
                                Log.d("Visualizer", "level=$level")
                            }
                        }

                        override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {

                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true,
                    false
                )
                enabled = true
            }
        } catch (e: Exception) {
            Log.e("AudioVisualizer", "Failed to setup Visualizer", e)
        }
    }

    fun stop() {
        visualizer?.enabled = false
        visualizer?.release()
        visualizer = null
    }
}