package com.example.echo.data.player

import android.media.audiofx.Visualizer
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class AudioVisualizer(private val exoPlayer: ExoPlayer) {

    private var visualizer: Visualizer? = null
    private var _visualData = MutableStateFlow(AudioVisualData())
    val visualData: Flow<AudioVisualData> = _visualData

    @OptIn(UnstableApi::class)
    fun start(): Flow<AudioVisualData> {
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
        return visualData
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

                                val boosted = (level * level).coerceIn(0f, 1f)

                                val hue = (1f - boosted) * 240f
                                val color = Color.hsl(hue, 0.9f, 0.6f)

                                _visualData.value = _visualData.value.copy(amplitude = level, color = color)
                                Log.d("Visualizer", "level=$level")
                            }
                        }

                        override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {
                            if (p1 != null){
                                val color = computeColorFromFft(p1)
                                _visualData.value = _visualData.value.copy(color = color)
                                Log.d("Visualizer", "Color: $color")
                            }
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

    private fun computeColorFromFft(fft: ByteArray): Color {
        val n = fft.size / 2
        var lowEnergy = 0f
        var highEnergy = 0f

        for (i in 0 until n) {
            val real = (fft[2 * i].toInt() and 0xFF) - 128
            val imag = (fft[2 * i + 1].toInt() and 0xFF) - 128
            val magnitude = Math.sqrt((real * real + imag * imag).toDouble()).toFloat()

            if (i < n * 0.15) lowEnergy += magnitude        // низкие частоты
            else if (i >= n * 0.5f) highEnergy += magnitude  // высокие частоты
        }

        val total = lowEnergy + highEnergy
        if (total == 0f) return Color(0xFF4FC3F7) // голубой по умолчанию

        // Доля низких частот: 0 = только высокие, 1 = только низкие
        var lowRatio = (lowEnergy / total).coerceIn(0f, 1f)
        lowRatio = (lowRatio * lowRatio)
        val hue = (1f - lowRatio) * 300f

        // Насыщенность и яркость фиксированные, чтобы цвета были сочными
        return Color.hsl(hue, 0.9f, 0.6f)
    }
}