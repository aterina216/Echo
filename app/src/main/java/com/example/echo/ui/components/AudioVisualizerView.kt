package com.example.echo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlinx.coroutines.flow.Flow

@Composable
fun AudioVisualizerView(amplitudeFlow: Flow<Float>, modifier: Modifier = Modifier) {
    val amplitude by amplitudeFlow.collectAsState(0f)

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)){
        val barCount = 20
        val barWidth = size.width / barCount
        val maxBarHeight = size.height

        val amp = amplitude
        for(i in 0 until barCount) {
            val randomFactor = (0.5f + 0.5f * Math.sin(i * 0.7 + (System.currentTimeMillis() % 5000)
            / 5000.0 * Math.PI)).toFloat()
            val barHeight = (amp * maxBarHeight * 0.8f + maxBarHeight * 0.2f * randomFactor).coerceIn(0f, maxBarHeight)
            drawRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(i * barWidth, size.height - barHeight),
                size = Size(barWidth * 0.8f, barHeight)
            )
        }
    }

}