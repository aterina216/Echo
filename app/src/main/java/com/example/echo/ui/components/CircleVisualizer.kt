package com.example.echo.ui.components

import android.R
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.echo.data.player.AudioVisualData
import com.example.echo.ui.functions.lerpColor
import com.example.echo.ui.theme.EchoThemeOption

@Composable
fun CircleVisualizer(
    audioData: AudioVisualData?,
    themeOption: EchoThemeOption,
    modifier: Modifier = Modifier
) {
    val targetAmplitude = audioData?.amplitude ?: 0f
    val smoothAmplitude by animateFloatAsState(
        targetAmplitude,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "smoothAmplitude"
    )

    val infiniteTransition = rememberInfiniteTransition("circle")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val baseColor = lerpColor(themeOption.waveColorDark, themeOption.waveColorLight,
        smoothAmplitude)

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val center = Offset(size.width/2, size.height/2)
        val baseRadius = size.minDimension * 0.35f
        val maxExtra = size.minDimension * 0.25f
        val radius = baseRadius + smoothAmplitude * maxExtra * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    themeOption.waveColorLight.copy(alpha = 0.8f),
                    themeOption.waveColor.copy(alpha = 0.3f)
                ),
                center = center,
                radius = radius
            ),
            radius, center
        )

        val glowLayers = 3
        for (i in glowLayers downTo 1) {
            val layerRadius = radius * (1f + i * 0.2f)
            val alpha = 0.15f / i
            drawCircle(
                color = baseColor.copy(alpha),
                layerRadius,
                center,
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    baseColor.copy(alpha = 0.7f),
                    baseColor.copy(alpha =  0.2f)
                ),
                center, radius
            )
        )
    }
}