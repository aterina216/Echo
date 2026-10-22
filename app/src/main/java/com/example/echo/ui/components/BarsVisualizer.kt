package com.example.echo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.echo.data.player.AudioVisualData
import com.example.echo.ui.functions.lerpColor
import com.example.echo.ui.theme.EchoThemeOption
import kotlin.math.sin


@Composable
fun BarsVisualizer(
    audioVisualData: AudioVisualData?,
    themeOption: EchoThemeOption,
    modifier: Modifier = Modifier
) {

    val targetAmplitude = audioVisualData?.amplitude ?: 0f
    val smoothAmplitude by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "smoothAmplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "bars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)){
        val barCount = 16
        val barWidth = size.width / barCount
        val maxHeight = size.height

        for (i in 0 until barCount){
            val phase = time * 2 * Math.PI.toFloat() + i * 0.4f
            val factor = (sin(phase) + 1f) / 2f
            val barHeight = (smoothAmplitude * factor * maxHeight).coerceAtLeast(8.dp.toPx())

            val intensity = (factor * 1.5f).coerceIn(0f, 1f)
            val barColor = lerpColor(themeOption.waveColor, themeOption.waveColorLight, intensity)

            val x = i * barWidth
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor.copy(alpha = 0.9f),
                topLeft = Offset(x, y),
                size = Size(barWidth * 0.7f, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}