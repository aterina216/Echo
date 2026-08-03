package com.example.echo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.echo.data.player.AudioVisualData
import kotlinx.coroutines.flow.Flow
import kotlin.io.path.Path
import kotlin.math.sin

@Composable
fun AudioVisualizerView(visualDataFlow: Flow<AudioVisualData>, modifier: Modifier = Modifier) {

    val visualData by visualDataFlow.collectAsState(AudioVisualData())

    val smoothAmplitude by animateFloatAsState(
        targetValue = visualData.amplitude,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "smoothAmplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)){
        val width = size.width
        val height = size.height
        val centerY = height/2
        val waveAmplitude = (smoothAmplitude * height * 0.5f).coerceAtLeast(15f)
        val frequency = 0.015f
        val speed = 5f
        val path = Path()

        path.moveTo(0f, centerY)
        for(x in 0..width.toInt() step 5) {
            val phase = time * 2 * Math.PI.toFloat() * speed + x * frequency
            val y = centerY + sin(phase) * waveAmplitude
            path.lineTo(x.toFloat(), y)
        }

        drawPath(
            path = path,
            color = visualData.color.copy(alpha = 0.8f),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val path2 = Path()
        path2.moveTo(0f, centerY)
        for (x in 0.. width.toInt() step 5) {
            val phase2 = time * speed * 0.7f + x * frequency * 1.5f + 2f
            val y2 = centerY + sin(phase2) * waveAmplitude * 0.6f
            path2.lineTo(x.toFloat(), y2)
        }

        drawPath(
            path = path2,
            color = visualData.color.copy(alpha = 0.3f),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

